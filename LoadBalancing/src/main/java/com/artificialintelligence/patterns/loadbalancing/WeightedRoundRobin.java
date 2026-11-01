package com.artificialintelligence.patterns.loadbalancing;

import java.util.List;

public class WeightedRoundRobin extends WeightedRoundRobinLoadBalancer {
    public WeightedRoundRobin() { super(); }
    public WeightedRoundRobin(List<?> servers) { super(servers); }
    public WeightedRoundRobin(Object ignored) { super(ignored); }
}