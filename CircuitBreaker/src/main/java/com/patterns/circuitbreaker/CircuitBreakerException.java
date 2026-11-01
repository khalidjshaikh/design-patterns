package com.patterns.circuitbreaker;

/**
 * Base exception for circuit breaker related failures.
 */
public class CircuitBreakerException extends RuntimeException {

    private final String circuitBreakerName;

    public CircuitBreakerException(String circuitBreakerName) {
        super("Circuit breaker '" + circuitBreakerName + "' error");
        this.circuitBreakerName = circuitBreakerName;
    }

    public CircuitBreakerException(String circuitBreakerName, String message) {
        super(message);
        this.circuitBreakerName = circuitBreakerName;
    }

    public CircuitBreakerException(String circuitBreakerName, Throwable cause) {
        super("Circuit breaker '" + circuitBreakerName + "' error", cause);
        this.circuitBreakerName = circuitBreakerName;
    }

    public String getCircuitBreakerName() {
        return circuitBreakerName;
    }
}