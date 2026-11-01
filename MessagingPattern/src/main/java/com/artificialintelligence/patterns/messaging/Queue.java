package com.artificialintelligence.patterns.messaging;

/** Alias for {@link PointToPointChannel}. */
public class Queue extends PointToPointChannel {
    public Queue(String name) { super(name); }
    public Queue(String name, int capacity) { super(name, capacity); }
}