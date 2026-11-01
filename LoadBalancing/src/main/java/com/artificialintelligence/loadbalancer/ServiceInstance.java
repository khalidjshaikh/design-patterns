package com.artificialintelligence.loadbalancer;

public class ServiceInstance extends Server {
    public ServiceInstance(String id) { super(id); }
    public ServiceInstance(String id, String serviceName, String host, int port) { super(id, host, port); }
    public ServiceInstance(String id, String serviceName, String host, int port, int weight) { super(id, host, port, weight); }
    public ServiceInstance(String id, String serviceName, String host, int port, int weight, boolean healthy) { super(id, host, port, weight, healthy); }
    public ServiceInstance(String id, String host, int port) { super(id, host, port); }
    public ServiceInstance(String name, int port) { super(name, port); }

    // compatibility with ServiceDiscovery naming
    public String getServiceId() { return getId(); }
    public String getServiceName() { return getId(); }
}