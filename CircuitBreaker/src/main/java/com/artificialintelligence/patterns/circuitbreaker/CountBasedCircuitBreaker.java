package com.artificialintelligence.patterns.circuitbreaker;

public class CountBasedCircuitBreaker extends CircuitBreaker {
    public CountBasedCircuitBreaker() { super(); }
    public CountBasedCircuitBreaker(String name) { super(name); }
    public CountBasedCircuitBreaker(CircuitBreakerConfig config) { super(config); }
    public CountBasedCircuitBreaker(String name, CircuitBreakerConfig config) { super(name, config); }
    public CountBasedCircuitBreaker(int failureThreshold, long resetTimeoutMs) { super(failureThreshold, resetTimeoutMs); }
}
