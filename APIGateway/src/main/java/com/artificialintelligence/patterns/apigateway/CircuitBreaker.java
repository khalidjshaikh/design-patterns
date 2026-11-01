package com.artificialintelligence.patterns.apigateway;

import java.util.function.Supplier;

/**
 * Simple circuit breaker: opens after a threshold of failures and rejects calls
 * (returning a fallback) until a reset timeout elapses, then probes in half-open state.
 */
public class CircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    private final int failureThreshold;
    private final long resetTimeoutMs;

    private State state = State.CLOSED;
    private int failures = 0;
    private long openedAt = 0L;
    private final Object lock = new Object();

    public CircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        if (failureThreshold <= 0) throw new IllegalArgumentException("failureThreshold must be > 0");
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
    }

    public <T> T call(Supplier<T> action, Supplier<T> fallback) {
        synchronized (lock) {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - openedAt >= resetTimeoutMs) {
                    state = State.HALF_OPEN;
                } else {
                    return fallback.get();
                }
            }
        }
        try {
            T result = action.get();
            onSuccess();
            return result;
        } catch (RuntimeException e) {
            onFailure();
            return fallback.get();
        }
    }

    private void onSuccess() {
        synchronized (lock) {
            failures = 0;
            if (state == State.HALF_OPEN) {
                state = State.CLOSED;
            }
        }
    }

    private void onFailure() {
        synchronized (lock) {
            failures++;
            if (state == State.HALF_OPEN || failures >= failureThreshold) {
                state = State.OPEN;
                openedAt = System.currentTimeMillis();
            }
        }
    }

    public State state() {
        synchronized (lock) {
            if (state == State.OPEN && System.currentTimeMillis() - openedAt >= resetTimeoutMs) {
                return State.HALF_OPEN;
            }
            return state;
        }
    }

    public void reset() {
        synchronized (lock) {
            state = State.CLOSED;
            failures = 0;
            openedAt = 0L;
        }
    }
}
