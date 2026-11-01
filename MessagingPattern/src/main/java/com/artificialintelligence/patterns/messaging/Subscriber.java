package com.artificialintelligence.patterns.messaging;

/**
 * Subscribes to / consumes messages from a channel.
 */
public class Subscriber {
    private final MessageConsumer consumer;
    public Subscriber(MessageChannel channel) { this.consumer = new MessageConsumer(channel); }
    public Message receive() { return consumer.receive(); }
    public Message receive(long timeoutMillis) { return consumer.receive(timeoutMillis); }
    public void subscribe(MessageListener listener) { consumer.subscribe(listener); }
    public MessageChannel getChannel() { return consumer.getChannel(); }
}