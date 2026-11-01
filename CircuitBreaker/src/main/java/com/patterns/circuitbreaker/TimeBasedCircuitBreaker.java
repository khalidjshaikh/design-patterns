package com.patterns.circuitbreaker;

public class TimeBasedCircuitBreaker extends CircuitBreaker {
    public TimeBasedCircuitBreaker() { super(); }
    public TimeBasedCircuitBreaker(String name) { super(name); }
    public TimeBasedCircuitBreaker(CircuitBreakerConfig config) { super(config); }
    public TimeBasedCircuitBreaker(String name, CircuitBreakerConfig config) { super(name, config); }
    public TimeBasedCircuitBreaker(int failureThreshold, long resetTimeoutMs) { super(failureThreshold, resetTimeoutMs); }
}
