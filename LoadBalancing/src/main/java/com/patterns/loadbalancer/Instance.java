package com.patterns.loadbalancer;

public class Instance extends Server {
    public Instance(String id) { super(id); }
    public Instance(String id, String host, int port) { super(id, host, port); }
    public Instance(String id, String host, int port, int weight) { super(id, host, port, weight); }
    public Instance(String id, String host, int port, int weight, boolean healthy) { super(id, host, port, weight, healthy); }
    public Instance(String name, int port) { super(name, port); }
}