package com.artificialintelligence.patterns.loadbalancer;

public class Backend extends Server {
    public Backend(String id) { super(id); }
    public Backend(String id, String host, int port) { super(id, host, port); }
    public Backend(String id, String host, int port, int weight) { super(id, host, port, weight); }
    public Backend(String id, String host, int port, int weight, boolean healthy) { super(id, host, port, weight, healthy); }
    public Backend(String name, int port) { super(name, port); }
}