package com.artificialintelligence.messaging;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Ensures a message is processed only once based on its id.
 */
public class IdempotentReceiver {

    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    public boolean isProcessed(Message message) {
        return message != null && processed.contains(message.id());
    }

    public boolean process(Message message, Consumer<Message> handler) {
        if (message == null || handler == null) {
            throw new IllegalArgumentException("message and handler must not be null");
        }
        if (!processed.add(message.id())) {
            return false; // already processed
        }
        try {
            handler.accept(message);
            return true;
        } catch (RuntimeException e) {
            processed.remove(message.id());
            throw e;
        }
    }

    public void reset() {
        processed.clear();
    }

    public int processedCount() {
        return processed.size();
    }
}