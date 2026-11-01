package com.artificialintelligence.patterns.messaging;

/**
 * Publishes messages to a channel (alias-style helper).
 */
public class Publisher {
    private final MessageProducer producer;
    public Publisher(MessageChannel channel) { this.producer = new MessageProducer(channel); }
    public Publisher(MessageBroker broker, String channelName) { this.producer = new MessageProducer(broker, channelName); }
    public void publish(Message message) { producer.send(message); }
    public void publish(Object payload) { producer.send(payload); }
    public MessageChannel getChannel() { return producer.getChannel(); }
}