package com.artificialintelligence.patterns.apigateway;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of downstream services keyed by name.
 */
public class ServiceRegistry {

    private final Map<String, DownstreamService> services = new ConcurrentHashMap<>();

    public void register(DownstreamService service) {
        services.put(service.name(), service);
    }

    public DownstreamService get(String name) {
        return services.get(name);
    }

    public boolean contains(String name) {
        return services.containsKey(name);
    }

    public Collection<DownstreamService> all() {
        return services.values();
    }

    public int size() {
        return services.size();
    }
}
