package com.artificialintelligence.patterns.loadbalancing;

public class BackendServer extends Server {
    public BackendServer(String id) { super(id); }
    public BackendServer(String id, String host, int port) { super(id, host, port); }
    public BackendServer(String id, String host, int port, int weight) { super(id, host, port, weight); }
    public BackendServer(String id, String host, int port, int weight, boolean healthy) { super(id, host, port, weight, healthy); }
    public BackendServer(String name, int port) { super(name, port); }
}