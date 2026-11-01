package com.patterns.circuitbreaker;

/**
 * Thrown when a call is rejected because the circuit breaker is open.
 */
public class CircuitBreakerOpenException extends CircuitBreakerException {

    public CircuitBreakerOpenException(String circuitBreakerName) {
        super(circuitBreakerName, "Circuit breaker '" + circuitBreakerName + "' is open: call not permitted");
    }
}