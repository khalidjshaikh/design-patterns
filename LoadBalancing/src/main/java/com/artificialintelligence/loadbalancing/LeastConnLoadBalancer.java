package com.artificialintelligence.loadbalancing;

import java.util.List;

public class LeastConnLoadBalancer extends LeastConnectionsLoadBalancer {
    public LeastConnLoadBalancer() { super(); }
    public LeastConnLoadBalancer(List<?> servers) { super(servers); }
    public LeastConnLoadBalancer(Object ignored) { super(ignored); }
}