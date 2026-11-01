package com.artificialintelligence.patterns.loadbalancer;

import java.util.List;
import java.util.function.Supplier;

public class LoadBalancedService {

    private final List<?> instances;
    private final LoadBalancer loadBalancer;

    public LoadBalancedService(List<?> instances) {
        this(instances, new RoundRobinLoadBalancer(instances));
    }

    public LoadBalancedService(List<?> instances, LoadBalancer loadBalancer) {
        this.instances = instances;
        this.loadBalancer = (loadBalancer == null) ? new RoundRobinLoadBalancer(instances) : loadBalancer;
    }

    public Object getInstance() {
        return loadBalancer.choose(instances);
    }

    public Server getServer() {
        return loadBalancer.choose(instances);
    }

    public <T> T execute(Supplier<T> task) {
        // select (and ignore) to keep balancing state, then run
        loadBalancer.choose(instances);
        return task.get();
    }

    public int instanceCount() {
        return instances == null ? 0 : instances.size();
    }
}