package com.patterns.loadbalancing;

import java.util.List;

public class LeastConnections extends LeastConnectionsLoadBalancer {
    public LeastConnections() { super(); }
    public LeastConnections(List<?> servers) { super(servers); }
    public LeastConnections(Object ignored) { super(ignored); }
}