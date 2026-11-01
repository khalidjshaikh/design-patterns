package com.artificialintelligence.patterns.messaging;

/** A dedicated channel for messages that could not be processed. */
public class DeadLetterChannel extends PointToPointChannel {
    public DeadLetterChannel(String name) { super(name); }
}