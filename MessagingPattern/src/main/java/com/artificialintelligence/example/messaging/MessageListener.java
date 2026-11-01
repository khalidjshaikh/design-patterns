package com.artificialintelligence.messaging;

/**
 * Callback invoked when a message is delivered.
 */
@FunctionalInterface
public interface MessageListener {
    void onMessage(Message message);
}