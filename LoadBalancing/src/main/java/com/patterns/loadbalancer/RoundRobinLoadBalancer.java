package com.patterns.loadbalancer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public RoundRobinLoadBalancer() {
        super();
    }

    public RoundRobinLoadBalancer(List<?> servers) {
        super(servers);
    }

    public RoundRobinLoadBalancer(Object ignored) {
        super();
    }

    @Override
    public Server choose(List<?> servers) {
        List<Server> candidates = healthyServers(servers);
        if (candidates.isEmpty()) {
            return null;
        }
        int index = Math.floorMod(counter.getAndIncrement(), candidates.size());
        return candidates.get(index);
    }

    @Override
    public Server choose(List<?> servers, String key) {
        List<Server> candidates = healthyServers(servers);
        if (candidates.isEmpty()) {
            return null;
        }
        String k = (key == null) ? "" : key;
        AtomicInteger c = counters.computeIfAbsent(k, x -> new AtomicInteger(0));
        int index = Math.floorMod(c.getAndIncrement(), candidates.size());
        return candidates.get(index);
    }

    // aliases
    public Server select(List<?> servers) { return choose(servers); }
    public Server next(List<?> servers) { return choose(servers); }
    public Server getNextServer(List<?> servers) { return choose(servers); }
    public Server getNextServer() { return next(); }
    public Server getServer() { return next(); }
    public Server balance() { return next(); }
}