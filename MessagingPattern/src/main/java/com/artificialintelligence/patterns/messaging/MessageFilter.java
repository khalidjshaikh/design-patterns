package com.artificialintelligence.patterns.messaging;

/**
 * Filters messages; returning {@code false} rejects the message.
 */
@FunctionalInterface
public interface MessageFilter {
    boolean accept(Message message);
}