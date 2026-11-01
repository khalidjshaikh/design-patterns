package com.artificialintelligence.patterns.messaging;

/**
 * Transforms a message into another message.
 */
@FunctionalInterface
public interface MessageTranslator {
    Message translate(Message message);
}