package com.example.loadbalancer;

import java.util.List;

public class ConsistentHash extends ConsistentHashLoadBalancer {
    public ConsistentHash() { super(); }
    public ConsistentHash(List<?> servers) { super(servers); }
    public ConsistentHash(Object ignored) { super(ignored); }
}