package com.artificialintelligence.patterns.messaging;

/** Alias for {@link PointToPointChannel}. */
public class MessageQueue extends PointToPointChannel {
    public MessageQueue(String name) { super(name); }
    public MessageQueue(String name, int capacity) { super(name, capacity); }
}