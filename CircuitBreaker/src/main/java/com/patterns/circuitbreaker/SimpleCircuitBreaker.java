package com.patterns.circuitbreaker;

public class SimpleCircuitBreaker extends CircuitBreaker {
    public SimpleCircuitBreaker() { super(); }
    public SimpleCircuitBreaker(String name) { super(name); }
    public SimpleCircuitBreaker(CircuitBreakerConfig config) { super(config); }
    public SimpleCircuitBreaker(String name, CircuitBreakerConfig config) { super(name, config); }
    public SimpleCircuitBreaker(int failureThreshold, long resetTimeoutMs) { super(failureThreshold, resetTimeoutMs); }
}
