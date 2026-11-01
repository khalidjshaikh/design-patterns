package com.artificialintelligence.loadbalancing;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalancer extends AbstractLoadBalancer {

    private final Random random;

    public RandomLoadBalancer() {
        this(ThreadLocalRandom.current());
    }

    public RandomLoadBalancer(long seed) {
        this(new Random(seed));
    }

    public RandomLoadBalancer(Random random) {
        super();
        this.random = (random == null) ? ThreadLocalRandom.current() : random;
    }

    public RandomLoadBalancer(List<?> servers) {
        this(servers, ThreadLocalRandom.current());
    }

    public RandomLoadBalancer(List<?> servers, long seed) {
        this(servers, new Random(seed));
    }

    public RandomLoadBalancer(List<?> servers, Random random) {
        super(servers);
        this.random = (random == null) ? ThreadLocalRandom.current() : random;
    }

    public RandomLoadBalancer(Object ignored) {
        this();
    }

    @Override
    public Server choose(List<?> servers) {
        List<Server> candidates = healthyServers(servers);
        if (candidates.isEmpty()) {
            return null;
        }
        int index = random.nextInt(candidates.size());
        return candidates.get(index);
    }

    @Override
    public Server choose(List<?> servers, String key) {
        return choose(servers);
    }

    public Server getNextServer(List<?> servers) { return choose(servers); }
    public Server getNextServer() { return next(); }
    public Server getServer() { return next(); }
}