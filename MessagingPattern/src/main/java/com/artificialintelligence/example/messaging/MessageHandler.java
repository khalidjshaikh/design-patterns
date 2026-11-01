package com.artificialintelligence.messaging;

/**
 * Handles a message.
 */
@FunctionalInterface
public interface MessageHandler {
    void handle(Message message);
}