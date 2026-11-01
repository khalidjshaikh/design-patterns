package com.artificialintelligence.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Point-to-point channel: each message is delivered to exactly one consumer.
 */
public class PointToPointChannel implements MessageChannel {

    private final String name;
    private final BlockingQueue<Message> queue;

    public PointToPointChannel(String name) {
        this(name, Integer.MAX_VALUE);
    }

    public PointToPointChannel(String name, int capacity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.name = name;
        this.queue = new LinkedBlockingQueue<>(capacity == Integer.MAX_VALUE ? Integer.MAX_VALUE : capacity);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void send(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while sending message", e);
        }
    }

    @Override
    public Message receive() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public Message receive(long timeoutMillis) {
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("timeoutMillis must be non-negative");
        }
        try {
            return queue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public List<Message> snapshot() {
        return new ArrayList<>(queue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointToPointChannel)) return false;
        PointToPointChannel that = (PointToPointChannel) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "PointToPointChannel{name='" + name + "', size=" + size() + "}";
    }
}