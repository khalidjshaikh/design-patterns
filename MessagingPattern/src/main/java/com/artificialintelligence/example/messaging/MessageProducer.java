package com.artificialintelligence.messaging;

import java.util.Map;

/**
 * Produces and sends messages to a channel or broker.
 */
public class MessageProducer {

    private final MessageChannel channel;
    private final MessageBroker broker;
    private final String channelName;

    public MessageProducer(MessageChannel channel) {
        this.channel = channel;
        this.broker = null;
        this.channelName = channel == null ? null : channel.name();
    }

    public MessageProducer(MessageBroker broker, String channelName) {
        this.broker = broker;
        this.channelName = channelName;
        this.channel = broker == null ? null : broker.getChannel(channelName);
    }

    public void send(Message message) {
        MessageChannel target = resolveChannel();
        if (target == null) {
            throw new IllegalStateException("no target channel");
        }
        target.send(message);
    }

    public void send(Object payload) {
        send(Message.of(payload));
    }

    public void send(String type, Object payload) {
        send(Message.of(type, payload));
    }

    public void send(String type, Object payload, Map<String, String> headers) {
        send(Message.of(type, payload, headers));
    }

    public MessageChannel getChannel() {
        return resolveChannel();
    }

    private MessageChannel resolveChannel() {
        if (channel != null) {
            return channel;
        }
        if (broker != null && channelName != null) {
            MessageChannel ch = broker.getChannel(channelName);
            if (ch == null) {
                ch = broker.createQueue(channelName);
            }
            return ch;
        }
        return null;
    }
}