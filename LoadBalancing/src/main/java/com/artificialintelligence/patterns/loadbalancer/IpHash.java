package com.artificialintelligence.patterns.loadbalancer;

import java.util.List;

public class IpHash extends IpHashLoadBalancer {
    public IpHash() { super(); }
    public IpHash(List<?> servers) { super(servers); }
    public IpHash(Object ignored) { super(ignored); }
}