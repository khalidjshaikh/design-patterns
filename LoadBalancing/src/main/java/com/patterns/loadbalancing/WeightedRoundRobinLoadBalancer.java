package com.patterns.loadbalancing;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRoundRobinLoadBalancer extends AbstractLoadBalancer {

    private final AtomicInteger counter = new AtomicInteger(0);

    public WeightedRoundRobinLoadBalancer() {
        super();
    }

    public WeightedRoundRobinLoadBalancer(List<?> servers) {
        super(servers);
    }

    public WeightedRoundRobinLoadBalancer(Object ignored) {
        super();
    }

    @Override
    public Server choose(List<?> servers) {
        List<Server> candidates = healthyServers(servers);
        if (candidates.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (Server s : candidates) {
            totalWeight += Math.max(1, s.getWeight());
        }
        if (totalWeight <= 0) {
            totalWeight = candidates.size();
        }
        int pos = Math.floorMod(counter.getAndIncrement(), totalWeight);

        int cumulative = 0;
        for (Server s : candidates) {
            cumulative += Math.max(1, s.getWeight());
            if (pos < cumulative) {
                return s;
            }
        }
        return candidates.get(candidates.size() - 1);
    }

    @Override
    public Server choose(List<?> servers, String key) {
        return choose(servers);
    }

    public Server getNextServer(List<?> servers) { return choose(servers); }
    public Server getNextServer() { return next(); }
    public Server getServer() { return next(); }
}