package com.artificialintelligence.messaging;

/**
 * Consumes messages from a channel.
 */
public class MessageConsumer {

    private final MessageChannel channel;

    public MessageConsumer(MessageChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }
        this.channel = channel;
    }

    public Message receive() {
        return channel.receive();
    }

    public Message receive(long timeoutMillis) {
        return channel.receive(timeoutMillis);
    }

    public void subscribe(MessageListener listener) {
        if (channel instanceof TopicChannel) {
            ((TopicChannel) channel).subscribe(listener);
        } else {
            throw new UnsupportedOperationException("subscribe is only supported on topic channels");
        }
    }

    public MessageChannel getChannel() {
        return channel;
    }
}