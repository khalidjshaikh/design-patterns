package com.patterns.loadbalancing;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {

    private static final int VIRTUAL_NODES = 100;

    public ConsistentHashLoadBalancer() {
        super();
    }

    public ConsistentHashLoadBalancer(List<?> servers) {
        super(servers);
    }

    public ConsistentHashLoadBalancer(Object ignored) {
        super();
    }

    @Override
    public Server choose(List<?> servers) {
        return choose(servers, "default");
    }

    @Override
    public Server choose(List<?> servers, String key) {
        List<Server> candidates = healthyServers(servers);
        if (candidates.isEmpty()) {
            return null;
        }
        SortedMap<Integer, Server> ring = new TreeMap<>();
        for (Server s : candidates) {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                int hash = (s.getId() + "#" + i).hashCode();
                ring.put(hash, s);
            }
        }
        String k = (key == null) ? "" : key;
        int hash = k.hashCode();
        SortedMap<Integer, Server> tail = ring.tailMap(hash);
        int keyHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
        return ring.get(keyHash);
    }

    public Server getNextServer(List<?> servers) { return choose(servers); }
    public Server getNextServer() { return next(); }
    public Server getServer() { return next(); }
}