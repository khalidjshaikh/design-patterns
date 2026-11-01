package com.artificialintelligence.patterns.messaging;

import java.util.List;

/**
 * A broker that owns and manages named channels.
 */
public interface MessageBroker {

    MessageChannel createQueue(String name);

    MessageChannel createTopic(String name);

    MessageChannel getChannel(String name);

    default MessageChannel getQueue(String name) { return getChannel(name); }

    default MessageChannel getTopic(String name) { return getChannel(name); }

    boolean deleteChannel(String name);

    List<String> channelNames();

    List<String> queueNames();

    List<String> topicNames();

    void send(String channelName, Message message);

    default void publish(String channelName, Message message) { send(channelName, message); }

    Message receive(String channelName);

    Message receive(String channelName, long timeoutMillis);

    void shutdown();

    // ---- convenience aliases for compatibility ----
    default void enqueue(String channelName, Message message) { send(channelName, message); }
    default Message dequeue(String channelName) { return receive(channelName); }
    default Message dequeue(String channelName, long timeoutMillis) { return receive(channelName, timeoutMillis); }
    default void put(String channelName, Message message) { send(channelName, message); }
    default Message take(String channelName) { return receive(channelName); }
    default Message take(String channelName, long timeoutMillis) { return receive(channelName, timeoutMillis); }
}