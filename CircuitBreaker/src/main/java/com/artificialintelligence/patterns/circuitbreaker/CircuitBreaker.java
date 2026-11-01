package com.artificialintelligence.patterns.circuitbreaker;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Circuit Breaker pattern.
 *
 * <p>Protects downstream calls by tracking failures. When the number of consecutive
 * failures reaches the configured threshold the breaker transitions to {@link State#OPEN}
 * and rejects calls (either throwing {@link CircuitBreakerOpenException} or invoking a
 * supplied fallback) until the configured wait duration has elapsed. It then moves to
 * {@link State#HALF_OPEN} and allows a limited number of trial calls; successes close
 * the breaker again while a failure re-opens it.</p>
 */
public class CircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    private final String name;
    private final CircuitBreakerConfig config;

    private volatile State state = State.CLOSED;
    private int failureCount = 0;
    private int halfOpenSuccesses = 0;
    private int halfOpenCalls = 0;
    private long openedAt = 0L;
    private final Object lock = new Object();

    private final AtomicLong successfulCalls = new AtomicLong();
    private final AtomicLong failedCalls = new AtomicLong();
    private final AtomicLong notPermittedCalls = new AtomicLong();

    public CircuitBreaker() {
        this("default", CircuitBreakerConfig.ofDefaults());
    }

    public CircuitBreaker(String name) {
        this(name, CircuitBreakerConfig.ofDefaults());
    }

    public CircuitBreaker(CircuitBreakerConfig config) {
        this("default", config);
    }

    public CircuitBreaker(String name, CircuitBreakerConfig config) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
        this.config = config == null ? CircuitBreakerConfig.ofDefaults() : config;
    }

    /** Legacy constructor matching the simpler API used by the API Gateway demo. */
    public CircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        this("default", CircuitBreakerConfig.builder()
                .failureThreshold(failureThreshold)
                .waitDuration(Duration.ofMillis(resetTimeoutMs))
                .build());
    }

    public CircuitBreaker(String name, int failureThreshold, long resetTimeoutMs) {
        this(name, CircuitBreakerConfig.builder()
                .failureThreshold(failureThreshold)
                .waitDuration(Duration.ofMillis(resetTimeoutMs))
                .build());
    }

    public CircuitBreaker(int failureThreshold, long resetTimeoutMs, int permittedCallsInHalfOpen) {
        this("default", CircuitBreakerConfig.builder()
                .failureThreshold(failureThreshold)
                .waitDuration(Duration.ofMillis(resetTimeoutMs))
                .permittedNumberOfCallsInHalfOpenState(permittedCallsInHalfOpen)
                .build());
    }

    public String getName() {
        return name;
    }

    public CircuitBreakerConfig getConfig() {
        return config;
    }

    public State state() {
        synchronized (lock) {
            if (state == State.OPEN && elapsedSinceOpen() >= config.getWaitDuration().toMillis()) {
                state = State.HALF_OPEN;
                halfOpenCalls = 0;
                halfOpenSuccesses = 0;
            }
            return state;
        }
    }

    public State getState() {
        return state();
    }

    public boolean isClosed() {
        return state() == State.CLOSED;
    }

    public boolean isOpen() {
        return state() == State.OPEN;
    }

    public boolean isHalfOpen() {
        return state() == State.HALF_OPEN;
    }

    public <T> T call(Supplier<T> action) {
        return call(action, null);
    }

    public <T> T call(Supplier<T> action, Supplier<T> fallback) {
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        if (!tryAcquirePermission()) {
            notPermittedCalls.incrementAndGet();
            if (fallback != null) {
                return fallback.get();
            }
            throw new CircuitBreakerOpenException(name);
        }
        try {
            T result = action.get();
            onSuccess();
            return result;
        } catch (Throwable t) {
            onFailure();
            if (fallback != null) {
                return fallback.get();
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            throw new RuntimeException(t);
        }
    }

    public <T> T executeSupplier(Supplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("supplier must not be null");
        }
        if (!tryAcquirePermission()) {
            notPermittedCalls.incrementAndGet();
            throw new CircuitBreakerOpenException(name);
        }
        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (Throwable t) {
            onFailure();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            throw new RuntimeException(t);
        }
    }

    public void run(Runnable runnable) {
        executeRunnable(runnable);
    }

    public void executeRunnable(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable must not be null");
        }
        executeSupplier(() -> {
            runnable.run();
            return null;
        });
    }

    public boolean tryAcquirePermission() {
        synchronized (lock) {
            State current = state();
            if (current == State.OPEN) {
                return false;
            }
            if (current == State.HALF_OPEN) {
                if (halfOpenCalls >= config.getPermittedNumberOfCallsInHalfOpenState()) {
                    return false;
                }
                halfOpenCalls++;
                return true;
            }
            return true;
        }
    }

    public void releasePermission() {
        // no-op for circuit breaker; permissions are tracked implicitly
    }

    public boolean allowRequest() {
        return tryAcquirePermission();
    }

    public boolean isCallPermitted() {
        return tryAcquirePermission();
    }

    public void recordSuccess() {
        onSuccess();
    }

    public void recordFailure() {
        onFailure();
    }

    public void recordFailure(Throwable throwable) {
        onFailure();
    }

    public int getFailureCount() {
        // not tracked externally beyond state; return 0 placeholder
        return 0;
    }

    private void onSuccess() {
        successfulCalls.incrementAndGet();
        synchronized (lock) {
            if (state == State.HALF_OPEN) {
                halfOpenSuccesses++;
                if (halfOpenSuccesses >= config.getPermittedNumberOfCallsInHalfOpenState()) {
                    close();
                }
            } else {
                failureCount = 0;
            }
        }
    }

    private void onFailure() {
        failedCalls.incrementAndGet();
        synchronized (lock) {
            if (state == State.HALF_OPEN) {
                open();
            } else {
                failureCount++;
                if (failureCount >= config.getFailureThreshold()) {
                    open();
                }
            }
        }
    }

    private void open() {
        state = State.OPEN;
        openedAt = System.currentTimeMillis();
        halfOpenCalls = 0;
        halfOpenSuccesses = 0;
    }

    private void close() {
        state = State.CLOSED;
        failureCount = 0;
        halfOpenCalls = 0;
        halfOpenSuccesses = 0;
        openedAt = 0L;
    }

    public void reset() {
        synchronized (lock) {
            close();
        }
    }

    public void forceOpen() {
        synchronized (lock) {
            open();
        }
    }

    public void forceClosed() {
        synchronized (lock) {
            close();
        }
    }

    private long elapsedSinceOpen() {
        return System.currentTimeMillis() - openedAt;
    }

    public CircuitBreakerMetrics getMetrics() {
        return new CircuitBreakerMetrics() {
            @Override
            public long getNumberOfSuccessfulCalls() {
                return successfulCalls.get();
            }

            @Override
            public long getNumberOfFailedCalls() {
                return failedCalls.get();
            }

            @Override
            public long getNumberOfNotPermittedCalls() {
                return notPermittedCalls.get();
            }

            @Override
            public long getNumberOfCalls() {
                return successfulCalls.get() + failedCalls.get();
            }

            @Override
            public State getState() {
                return CircuitBreaker.this.state();
            }

            @Override
            public double getFailureRate() {
                long total = successfulCalls.get() + failedCalls.get();
                if (total == 0) {
                    return 0.0;
                }
                return (failedCalls.get() * 100.0) / total;
            }
        };
    }

    public long getNumberOfSuccessfulCalls() {
        return getMetrics().getNumberOfSuccessfulCalls();
    }

    public long getNumberOfFailedCalls() {
        return getMetrics().getNumberOfFailedCalls();
    }

    public long getNumberOfNotPermittedCalls() {
        return getMetrics().getNumberOfNotPermittedCalls();
    }
}