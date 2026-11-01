package com.artificialintelligence.messaging;

/** Alias for {@link RequestReplyTemplate}. */
public class RequestReply extends RequestReplyTemplate {
    public RequestReply(MessageChannel requestChannel, MessageBroker broker) { super(requestChannel, broker); }
    public RequestReply(MessageChannel requestChannel) { super(requestChannel); }
}