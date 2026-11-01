package com.artificialintelligence.patterns.messaging;

import java.util.UUID;
import java.util.function.Function;

/**
 * Implements the request-reply messaging pattern using temporary reply channels.
 */
public class RequestReplyTemplate {

    private final MessageChannel requestChannel;
    private final MessageBroker broker;
    private long defaultTimeoutMillis = 5000L;

    public RequestReplyTemplate(MessageChannel requestChannel, MessageBroker broker) {
        if (requestChannel == null) {
            throw new IllegalArgumentException("requestChannel must not be null");
        }
        this.requestChannel = requestChannel;
        this.broker = broker;
    }

    public RequestReplyTemplate(MessageChannel requestChannel) {
        this(requestChannel, null);
    }

    public void setDefaultTimeoutMillis(long defaultTimeoutMillis) {
        if (defaultTimeoutMillis <= 0) {
            throw new IllegalArgumentException("defaultTimeoutMillis must be positive");
        }
        this.defaultTimeoutMillis = defaultTimeoutMillis;
    }

    public Message request(Message request) {
        return request(request, defaultTimeoutMillis);
    }

    public Message request(Message request, long timeoutMillis) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        String correlationId = request.correlationId();
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        String replyQueueName = "reply-" + correlationId;
        MessageChannel replyChannel = null;
        if (broker != null) {
            replyChannel = broker.createQueue(replyQueueName);
        } else if (request.replyTo() != null) {
            replyQueueName = request.replyTo();
            // no broker; assume channel exists globally? fallback
        }

        Message requestMessage = request.withCorrelationId(correlationId).withReplyTo(replyQueueName);

        // If no broker, try to use a channel with that name from a global registry? best effort.
        requestChannel.send(requestMessage);

        Message reply = null;
        if (replyChannel != null) {
            reply = replyChannel.receive(timeoutMillis);
            broker.deleteChannel(replyQueueName);
        }
        return reply;
    }

    /**
     * Sends a reply to the channel indicated by {@code request.replyTo()}, preserving correlation id.
     */
    public static void reply(Message request, Message reply, MessageBroker broker) {
        if (request == null || reply == null) {
            throw new IllegalArgumentException("request and reply must not be null");
        }
        String target = request.replyTo();
        if (target == null || target.isBlank()) {
            throw new IllegalStateException("request has no replyTo channel");
        }
        Message replyMessage = reply.withCorrelationId(request.correlationId());
        if (broker != null) {
            broker.send(target, replyMessage);
        }
    }

    /**
     * Starts a background thread that handles requests from {@code requestChannel}
     * using the given handler and replies via the broker.
     */
    public static Thread startResponder(MessageChannel requestChannel, Function<Message, Message> handler,
                                        MessageBroker broker) {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message req = requestChannel.receive(1000);
                if (req == null) {
                    continue;
                }
                try {
                    Message reply = handler.apply(req);
                    reply(req, reply, broker);
                } catch (RuntimeException ignored) {
                    // ignore handler errors
                }
            }
        }, "request-reply-responder");
        t.setDaemon(true);
        t.start();
        return t;
    }
}