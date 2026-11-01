package com.artificialintelligence.messaging;

/** Alias for {@link MessageProducer}. */
public class Producer extends MessageProducer {
    public Producer(MessageChannel channel) { super(channel); }
    public Producer(MessageBroker broker, String channelName) { super(broker, channelName); }
}