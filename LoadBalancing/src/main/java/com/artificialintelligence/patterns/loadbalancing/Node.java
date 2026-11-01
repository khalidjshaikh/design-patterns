package com.artificialintelligence.patterns.loadbalancing;

public class Node extends Server {
    public Node(String id) { super(id); }
    public Node(String id, String host, int port) { super(id, host, port); }
    public Node(String id, String host, int port, int weight) { super(id, host, port, weight); }
    public Node(String id, String host, int port, int weight, boolean healthy) { super(id, host, port, weight, healthy); }
    public Node(String name, int port) { super(name, port); }
}