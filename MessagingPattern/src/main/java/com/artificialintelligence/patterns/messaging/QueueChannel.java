package com.artificialintelligence.patterns.messaging;

/** Alias for {@link PointToPointChannel}. */
public class QueueChannel extends PointToPointChannel {
    public QueueChannel(String name) { super(name); }
    public QueueChannel(String name, int capacity) { super(name, capacity); }
}