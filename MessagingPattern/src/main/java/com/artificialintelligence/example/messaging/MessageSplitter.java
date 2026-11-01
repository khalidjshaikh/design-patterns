package com.artificialintelligence.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Splits a single message into multiple messages.
 */
public class MessageSplitter {

    public List<Message> split(Message message, Function<Message, List<Object>> splitter) {
        if (message == null || splitter == null) {
            throw new IllegalArgumentException("message and splitter must not be null");
        }
        List<Message> result = new ArrayList<>();
        for (Object part : splitter.apply(message)) {
            result.add(Message.builder()
                    .type(message.type())
                    .headers(message.headers())
                    .payload(part)
                    .correlationId(message.correlationId())
                    .replyTo(message.replyTo())
                    .build());
        }
        return result;
    }

    public List<Message> splitCollection(Message message) {
        Object payload = message.payload();
        List<Object> parts = new ArrayList<>();
        if (payload instanceof Iterable) {
            for (Object o : (Iterable<?>) payload) {
                parts.add(o);
            }
        } else if (payload != null && payload.getClass().isArray()) {
            for (Object o : (Object[]) payload) {
                parts.add(o);
            }
        } else {
            parts.add(payload);
        }
        return split(message, m -> parts);
    }
}