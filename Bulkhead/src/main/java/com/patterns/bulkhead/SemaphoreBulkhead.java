package com.patterns.bulkhead;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

/**
 * Bulkhead implementation backed by a {@link Semaphore}. Calls that cannot acquire
 * a permit within the configured wait duration are rejected with
 * {@link BulkheadFullException}.
 */
public class SemaphoreBulkhead implements Bulkhead {

    private final String name;
    private final BulkheadConfig config;
    private final Semaphore semaphore;

    public SemaphoreBulkhead(String name) {
        this(name, BulkheadConfig.ofDefaults());
    }

    public SemaphoreBulkhead(String name, BulkheadConfig config) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
        this.config = config == null ? BulkheadConfig.ofDefaults() : config;
        this.semaphore = new Semaphore(this.config.getMaxConcurrentCalls(), true);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BulkheadMetrics getMetrics() {
        return new BulkheadMetrics() {
            @Override
            public int getAvailableConcurrentCalls() {
                return semaphore.availablePermits();
            }

            @Override
            public int getMaxAllowedConcurrentCalls() {
                return config.getMaxConcurrentCalls();
            }
        };
    }

    @Override
    public <T> T executeSupplier(Supplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("supplier must not be null");
        }
        if (!tryAcquirePermission()) {
            throw new BulkheadFullException(name);
        }
        try {
            return supplier.get();
        } finally {
            releasePermission();
        }
    }

    @Override
    public void executeRunnable(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable must not be null");
        }
        if (!tryAcquirePermission()) {
            throw new BulkheadFullException(name);
        }
        try {
            runnable.run();
        } finally {
            releasePermission();
        }
    }

    @Override
    public boolean tryAcquirePermission() {
        Duration wait = config.getMaxWaitDuration();
        try {
            if (wait.isZero() || wait.isNegative()) {
                return semaphore.tryAcquire();
            }
            return semaphore.tryAcquire(wait.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void releasePermission() {
        semaphore.release();
    }

    @Override
    public int getAvailableConcurrentCalls() {
        return semaphore.availablePermits();
    }

    @Override
    public int getMaxAllowedConcurrentCalls() {
        return config.getMaxConcurrentCalls();
    }
}
