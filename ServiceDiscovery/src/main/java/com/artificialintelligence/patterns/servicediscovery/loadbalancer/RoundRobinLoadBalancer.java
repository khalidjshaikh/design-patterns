package com.artificialintelligence.patterns.servicediscovery.loadbalancer;

import com.artificialintelligence.patterns.servicediscovery.model.ServiceInstance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-robin load balancer. Maintains a separate counter per service name
 * so that different services do not interfere with each other.
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        String key = instances.isEmpty() ? "" : instances.get(0).getServiceName();
        AtomicInteger counter = counters.computeIfAbsent(key, k -> new AtomicInteger(0));
        int index = Math.floorMod(counter.getAndIncrement(), instances.size());
        return instances.get(index);
    }
}
