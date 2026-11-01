package com.example.loadbalancing;

import java.util.List;
import java.util.Random;

public class RandomBalancer extends RandomLoadBalancer {
    public RandomBalancer() { super(); }
    public RandomBalancer(long seed) { super(seed); }
    public RandomBalancer(Random random) { super(random); }
    public RandomBalancer(List<?> servers) { super(servers); }
    public RandomBalancer(List<?> servers, long seed) { super(servers, seed); }
    public RandomBalancer(List<?> servers, Random random) { super(servers, random); }
    public RandomBalancer(Object ignored) { super(ignored); }
}
