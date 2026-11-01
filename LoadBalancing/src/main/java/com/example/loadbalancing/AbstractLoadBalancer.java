package com.example.loadbalancing;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLoadBalancer implements LoadBalancer {

    private List<?> servers = new ArrayList<>();

    public AbstractLoadBalancer() {
    }

    public AbstractLoadBalancer(List<?> servers) {
        setServers(servers);
    }

    public void setServers(List<?> servers) {
        this.servers = (servers == null) ? new ArrayList<>() : servers;
    }

    public List<?> getServers() {
        return servers;
    }

    public void addServer(Object server) {
        if (server == null) return;
        List<Object> list = new java.util.ArrayList<>();
        if (servers != null) list.addAll(servers);
        list.add(server);
        servers = list;
    }

    public void removeServer(Object server) {
        if (servers == null) return;
        List<Object> list = new java.util.ArrayList<>(servers);
        list.remove(server);
        servers = list;
    }

    public int size() {
        return servers == null ? 0 : servers.size();
    }

    public String getAlgorithm() {
        return getClass().getSimpleName();
    }

    protected List<Server> asServers(List<?> input) {
        List<Server> result = new ArrayList<>();
        if (input == null) return result;
        for (Object o : input) {
            if (o instanceof Server) {
                result.add((Server) o);
            }
        }
        return result;
    }

    protected List<Server> healthyServers(List<?> input) {
        List<Server> all = asServers(input);
        List<Server> healthy = new ArrayList<>();
        for (Server s : all) {
            if (s.isHealthy()) {
                healthy.add(s);
            }
        }
        if (healthy.isEmpty()) {
            healthy.addAll(all);
        }
        return healthy;
    }

    @Override
    public Server next() {
        return choose(getServers());
    }
}