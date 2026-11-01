package com.example.loadbalancing;

import java.util.List;

public class LeastConnectionsLoadBalancer extends AbstractLoadBalancer {

    public LeastConnectionsLoadBalancer() {
        super();
    }

    public LeastConnectionsLoadBalancer(List<?> servers) {
        super(servers);
    }

    public LeastConnectionsLoadBalancer(Object ignored) {
        super();
    }

    @Override
    public Server choose(List<?> servers) {
        List<Server> candidates = healthyServers(servers);
        if (candidates.isEmpty()) {
            return null;
        }
        Server chosen = candidates.get(0);
        for (Server s : candidates) {
            if (s.getActiveConnections() < chosen.getActiveConnections()) {
                chosen = s;
            }
        }
        return chosen;
    }

    @Override
    public Server choose(List<?> servers, String key) {
        return choose(servers);
    }

    public void release(Server server) {
        if (server != null) {
            server.decrementConnections();
        }
    }

    public void releaseConnection(Server server) {
        release(server);
    }

    public Server getNextServer(List<?> servers) { return choose(servers); }
    public Server getNextServer() { return next(); }
    public Server getServer() { return next(); }
}