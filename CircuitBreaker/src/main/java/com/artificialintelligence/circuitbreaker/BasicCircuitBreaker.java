package com.artificialintelligence.circuitbreaker;

public class BasicCircuitBreaker extends CircuitBreaker {
    public BasicCircuitBreaker() { super(); }
    public BasicCircuitBreaker(String name) { super(name); }
    public BasicCircuitBreaker(CircuitBreakerConfig config) { super(config); }
    public BasicCircuitBreaker(String name, CircuitBreakerConfig config) { super(name, config); }
    public BasicCircuitBreaker(int failureThreshold, long resetTimeoutMs) { super(failureThreshold, resetTimeoutMs); }
}
