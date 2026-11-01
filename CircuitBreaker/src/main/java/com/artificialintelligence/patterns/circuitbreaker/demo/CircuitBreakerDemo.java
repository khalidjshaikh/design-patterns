package com.artificialintelligence.patterns.circuitbreaker.demo;

import com.artificialintelligence.patterns.circuitbreaker.CircuitBreaker;
import com.artificialintelligence.patterns.circuitbreaker.CircuitBreakerConfig;
import com.artificialintelligence.patterns.circuitbreaker.CircuitBreakerOpenException;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreakerDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Circuit Breaker Demo ===");

        CircuitBreakerConfig config = CircuitBreakerConfig.builder()
                .failureThreshold(2)
                .waitDuration(Duration.ofMillis(200))
                .permittedNumberOfCallsInHalfOpenState(1)
                .build();
        CircuitBreaker cb = new CircuitBreaker("demo", config);

        AtomicInteger calls = new AtomicInteger(0);
        for (int i = 0; i < 6; i++) {
            int n = i;
            try {
                String result = cb.call(() -> {
                    if (calls.incrementAndGet() <= 2) {
                        throw new IllegalStateException("boom");
                    }
                    return "ok";
                }, () -> "fallback");
                System.out.println("call " + (n + 1) + " -> " + result + " (state=" + cb.state() + ")");
            } catch (CircuitBreakerOpenException e) {
                System.out.println("call " + (n + 1) + " -> rejected (state=" + cb.state() + ")");
            }
            Thread.sleep(120);
        }
    }
}