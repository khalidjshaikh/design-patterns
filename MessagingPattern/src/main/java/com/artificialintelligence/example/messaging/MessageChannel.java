package com.artificialintelligence.messaging;

import java.util.List;

/**
 * A channel over which messages are exchanged.
 */
public interface MessageChannel {

    /** Returns the name of this channel. */
    String name();

    /** Sends a message to the channel. */
    void send(Message message);

    /** Receives a message, blocking until one becomes available. */
    Message receive();

    /** Receives a message, waiting up to {@code timeoutMillis} milliseconds. */
    Message receive(long timeoutMillis);

    /** Returns the current number of queued messages. */
    int size();

    /** Returns {@code true} when no messages are queued. */
    boolean isEmpty();

    /** Removes all queued messages. */
    void clear();

    /** Returns a snapshot of queued messages (best effort). */
    List<Message> snapshot();

    // ---- convenience aliases for compatibility ----
    default void put(Message message) { send(message); }
    default Message take() { return receive(); }
    default Message take(long timeoutMillis) { return receive(timeoutMillis); }
    default void enqueue(Message message) { send(message); }
    default Message dequeue() { return receive(); }
    default Message dequeue(long timeoutMillis) { return receive(timeoutMillis); }
    default void offer(Message message) { send(message); }
    default Message poll() { return receive(0); }
    default Message poll(long timeoutMillis) { return receive(timeoutMillis); }
    default void publish(Message message) { send(message); }
}