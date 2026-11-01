package com.artificialintelligence.patterns.messaging;

/**
 * Handles a message.
 */
@FunctionalInterface
public interface MessageHandler {
    void handle(Message message);
}