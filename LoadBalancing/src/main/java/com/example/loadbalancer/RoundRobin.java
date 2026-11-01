package com.example.loadbalancer;

import java.util.List;

public class RoundRobin extends RoundRobinLoadBalancer {
    public RoundRobin() { super(); }
    public RoundRobin(List<?> servers) { super(servers); }
    public RoundRobin(Object ignored) { super(ignored); }
}