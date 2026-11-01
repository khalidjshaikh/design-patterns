package com.patterns.circuitbreaker;

public class SlidingWindowCircuitBreaker extends CircuitBreaker {
    public SlidingWindowCircuitBreaker() { super(); }
    public SlidingWindowCircuitBreaker(String name) { super(name); }
    public SlidingWindowCircuitBreaker(CircuitBreakerConfig config) { super(config); }
    public SlidingWindowCircuitBreaker(String name, CircuitBreakerConfig config) { super(name, config); }
    public SlidingWindowCircuitBreaker(int failureThreshold, long resetTimeoutMs) { super(failureThreshold, resetTimeoutMs); }
}
