package com.artificialintelligence.patterns.circuitbreaker;

public class DefaultCircuitBreaker extends CircuitBreaker {
    public DefaultCircuitBreaker() { super(); }
    public DefaultCircuitBreaker(String name) { super(name); }
    public DefaultCircuitBreaker(CircuitBreakerConfig config) { super(config); }
    public DefaultCircuitBreaker(String name, CircuitBreakerConfig config) { super(name, config); }
    public DefaultCircuitBreaker(int failureThreshold, long resetTimeoutMs) { super(failureThreshold, resetTimeoutMs); }
}