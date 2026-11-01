package com.artificialintelligence.circuitbreaker;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for named circuit breakers.
 */
public class CircuitBreakerRegistry {

    private final CircuitBreakerConfig defaultConfig;
    private final Map<String, CircuitBreaker> breakers = new ConcurrentHashMap<>();

    public CircuitBreakerRegistry() {
        this(CircuitBreakerConfig.ofDefaults());
    }

    public CircuitBreakerRegistry(CircuitBreakerConfig defaultConfig) {
        this.defaultConfig = defaultConfig == null ? CircuitBreakerConfig.ofDefaults() : defaultConfig;
    }

    public static CircuitBreakerRegistry ofDefaults() {
        return new CircuitBreakerRegistry();
    }

    public static CircuitBreakerRegistry of(CircuitBreakerConfig defaultConfig) {
        return new CircuitBreakerRegistry(defaultConfig);
    }

    public CircuitBreaker circuitBreaker(String name) {
        return circuitBreaker(name, defaultConfig);
    }

    public CircuitBreaker circuitBreaker(String name, CircuitBreakerConfig config) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        CircuitBreaker existing = breakers.get(name);
        if (existing != null) {
            return existing;
        }
        CircuitBreaker created = new CircuitBreaker(name, config == null ? defaultConfig : config);
        CircuitBreaker raced = breakers.putIfAbsent(name, created);
        return raced == null ? created : raced;
    }

    public CircuitBreaker get(String name) {
        return breakers.get(name);
    }

    public CircuitBreaker find(String name) {
        return get(name);
    }

    public CircuitBreaker getOrCreate(String name) {
        return circuitBreaker(name);
    }

    public CircuitBreaker getOrCreate(String name, CircuitBreakerConfig config) {
        return circuitBreaker(name, config);
    }

    public CircuitBreaker remove(String name) {
        return breakers.remove(name);
    }

    public Collection<CircuitBreaker> getAllCircuitBreakers() {
        return breakers.values();
    }

    public int size() {
        return breakers.size();
    }
}