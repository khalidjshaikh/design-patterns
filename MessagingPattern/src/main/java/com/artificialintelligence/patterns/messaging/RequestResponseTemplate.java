package com.artificialintelligence.patterns.messaging;

/** Alias for {@link RequestReplyTemplate}. */
public class RequestResponseTemplate extends RequestReplyTemplate {
    public RequestResponseTemplate(MessageChannel requestChannel, MessageBroker broker) { super(requestChannel, broker); }
    public RequestResponseTemplate(MessageChannel requestChannel) { super(requestChannel); }
}