package com.artificialintelligence.circuitbreaker;

/**
 * Top-level alias for {@link CircuitBreaker.State}.
 */
public enum CircuitBreakerState {
    CLOSED, OPEN, HALF_OPEN
}