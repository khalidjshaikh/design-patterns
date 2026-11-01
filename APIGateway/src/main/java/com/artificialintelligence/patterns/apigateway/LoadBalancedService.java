package com.artificialintelligence.patterns.apigateway;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A downstream service that load-balances across several instances using round-robin.
 */
public final class LoadBalancedService implements DownstreamService {

    private final String name;
    private final List<DownstreamService> instances;
    private final AtomicInteger counter = new AtomicInteger(0);

    public LoadBalancedService(String name, List<DownstreamService> instances) {
        if (instances == null || instances.isEmpty()) {
            throw new IllegalArgumentException("instances must not be empty");
        }
        this.name = name;
        this.instances = List.copyOf(instances);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        int idx = Math.floorMod(counter.getAndIncrement(), instances.size());
        return instances.get(idx).handle(request);
    }

    public int instanceCount() {
        return instances.size();
    }
}
