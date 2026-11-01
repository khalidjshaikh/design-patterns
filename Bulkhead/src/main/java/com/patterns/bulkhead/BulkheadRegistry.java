package com.patterns.bulkhead;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple registry for named bulkheads.
 */
public class BulkheadRegistry {

    private final Map<String, Bulkhead> bulkheads = new ConcurrentHashMap<>();

    public Bulkhead register(Bulkhead bulkhead) {
        if (bulkhead == null) {
            throw new IllegalArgumentException("bulkhead must not be null");
        }
        Bulkhead existing = bulkheads.putIfAbsent(bulkhead.getName(), bulkhead);
        return existing == null ? bulkhead : existing;
    }

    public Bulkhead get(String name) {
        return bulkheads.get(name);
    }

    public Bulkhead remove(String name) {
        return bulkheads.remove(name);
    }

    public int size() {
        return bulkheads.size();
    }
}
