package com.artificialintelligence.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of {@link MessageBroker}.
 */
public class InMemoryMessageBroker implements MessageBroker {

    public enum ChannelType { QUEUE, TOPIC }

    private final Map<String, MessageChannel> channels = new ConcurrentHashMap<>();
    private final Map<String, ChannelType> types = new ConcurrentHashMap<>();
    private volatile boolean shutdown = false;

    @Override
    public MessageChannel createQueue(String name) {
        validateName(name);
        ensureNotShutdown();
        MessageChannel existing = channels.get(name);
        if (existing != null) {
            return existing;
        }
        PointToPointChannel channel = new PointToPointChannel(name);
        MessageChannel prev = channels.putIfAbsent(name, channel);
        if (prev != null) {
            return prev;
        }
        types.put(name, ChannelType.QUEUE);
        return channel;
    }

    @Override
    public MessageChannel createTopic(String name) {
        validateName(name);
        ensureNotShutdown();
        MessageChannel existing = channels.get(name);
        if (existing != null) {
            return existing;
        }
        TopicChannel channel = new TopicChannel(name);
        MessageChannel prev = channels.putIfAbsent(name, channel);
        if (prev != null) {
            return prev;
        }
        types.put(name, ChannelType.TOPIC);
        return channel;
    }

    public MessageChannel createChannel(String name, ChannelType type) {
        if (type == ChannelType.TOPIC) {
            return createTopic(name);
        }
        return createQueue(name);
    }

    @Override
    public MessageChannel getChannel(String name) {
        return channels.get(name);
    }

    @Override
    public boolean deleteChannel(String name) {
        MessageChannel removed = channels.remove(name);
        types.remove(name);
        if (removed instanceof PointToPointChannel) {
            ((PointToPointChannel) removed).clear();
        }
        return removed != null;
    }

    @Override
    public List<String> channelNames() {
        return new ArrayList<>(channels.keySet());
    }

    @Override
    public List<String> queueNames() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, ChannelType> e : types.entrySet()) {
            if (e.getValue() == ChannelType.QUEUE) {
                result.add(e.getKey());
            }
        }
        return result;
    }

    @Override
    public List<String> topicNames() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, ChannelType> e : types.entrySet()) {
            if (e.getValue() == ChannelType.TOPIC) {
                result.add(e.getKey());
            }
        }
        return result;
    }

    @Override
    public void send(String channelName, Message message) {
        ensureNotShutdown();
        MessageChannel channel = channels.get(channelName);
        if (channel == null) {
            // Auto-create a queue for convenience.
            channel = createQueue(channelName);
        }
        channel.send(message);
    }

    @Override
    public Message receive(String channelName) {
        MessageChannel channel = channels.get(channelName);
        if (channel == null) {
            return null;
        }
        return channel.receive();
    }

    @Override
    public Message receive(String channelName, long timeoutMillis) {
        MessageChannel channel = channels.get(channelName);
        if (channel == null) {
            return null;
        }
        return channel.receive(timeoutMillis);
    }

    @Override
    public void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;
        for (MessageChannel channel : new ArrayList<>(channels.values())) {
            channel.clear();
        }
        channels.clear();
        types.clear();
    }

    public boolean isShutdown() {
        return shutdown;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    private void ensureNotShutdown() {
        if (shutdown) {
            throw new IllegalStateException("broker is shut down");
        }
    }

    public Map<String, ChannelType> channelTypes() {
        return Collections.unmodifiableMap(types);
    }
}