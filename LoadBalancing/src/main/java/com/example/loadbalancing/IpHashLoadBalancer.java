package com.example.loadbalancing;

import java.util.List;

public class IpHashLoadBalancer extends AbstractLoadBalancer {

    public IpHashLoadBalancer() {
        super();
    }

    public IpHashLoadBalancer(List<?> servers) {
        super(servers);
    }

    public IpHashLoadBalancer(Object ignored) {
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
        String k = (key == null) ? "" : key;
        int hash = k.hashCode();
        int index = Math.floorMod(hash, candidates.size());
        return candidates.get(index);
    }

    public Server getServer(String key, List<?> servers) {
        return choose(servers, key);
    }

    public Server getNextServer(List<?> servers) { return choose(servers); }
    public Server getNextServer() { return next(); }
    public Server getServer() { return next(); }
}



