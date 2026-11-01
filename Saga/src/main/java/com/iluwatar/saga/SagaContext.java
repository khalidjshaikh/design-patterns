package com.iluwatar.saga;

import java.util.Map;

/**
 * Shared, mutable context passed to every step of a saga.
 */
public interface SagaContext {

    void put(String key, Object value);

    <T> T get(String key);

    @SuppressWarnings("unchecked")
    default <T> T get(String key, Class<T> type) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Value for key '" + key + "' is not of type " + type.getName());
    }

    boolean contains(String key);

    void remove(String key);

    Map<String, Object> asMap();

    /**
     * Returns an immutable snapshot of the current context.
     */
    default Map<String, Object> snapshot() {
        return Map.copyOf(asMap());
    }
}
