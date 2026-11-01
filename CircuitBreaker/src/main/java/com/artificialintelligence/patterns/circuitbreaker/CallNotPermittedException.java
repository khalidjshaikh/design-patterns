package com.artificialintelligence.patterns.circuitbreaker;

/**
 * Alias for {@link CircuitBreakerOpenException} (Resilience4j-style naming).
 */
public class CallNotPermittedException extends CircuitBreakerOpenException {

    public CallNotPermittedException(String circuitBreakerName) {
        super(circuitBreakerName);
    }
}