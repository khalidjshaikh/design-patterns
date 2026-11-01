package com.artificialintelligence.patterns.circuitbreaker;

/**
 * Snapshot of a circuit breaker's call metrics.
 */
public interface CircuitBreakerMetrics {

    long getNumberOfSuccessfulCalls();

    long getNumberOfFailedCalls();

    long getNumberOfNotPermittedCalls();

    long getNumberOfCalls();

    CircuitBreaker.State getState();

    double getFailureRate();
}