package com.patterns.saga;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Default in-memory {@link SagaContext} implementation.
 */
public class DefaultSagaContext implements SagaContext {

    private final Map<String, Object> data = new LinkedHashMap<>();

    public DefaultSagaContext() {
    }

    public DefaultSagaContext(Map<String, Object> initial) {
        if (initial != null) {
            data.putAll(initial);
        }
    }

    @Override
    public void put(String key, Object value) {
        Objects.requireNonNull(key, "key");
        data.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    @Override
    public boolean contains(String key) {
        return data.containsKey(key);
    }

    @Override
    public void remove(String key) {
        data.remove(key);
    }

    @Override
    public Map<String, Object> asMap() {
        return data;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
