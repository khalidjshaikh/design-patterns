package com.artificialintelligence.loadbalancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancerRegistry {

    private final Map<String, LoadBalancer> balancers = new ConcurrentHashMap<>();

    public LoadBalancer register(String name, LoadBalancer lb) {
        if (lb == null) {
            throw new IllegalArgumentException("lb must not be null");
        }
        LoadBalancer existing = balancers.putIfAbsent(name, lb);
        return existing == null ? lb : existing;
    }

    public LoadBalancer get(String name) {
        return balancers.get(name);
    }

    public LoadBalancer remove(String name) {
        return balancers.remove(name);
    }

    public int size() {
        return balancers.size();
    }
}