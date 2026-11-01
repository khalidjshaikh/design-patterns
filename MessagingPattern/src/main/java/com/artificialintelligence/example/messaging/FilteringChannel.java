package com.artificialintelligence.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A channel wrapper that applies a {@link MessageFilter} and forwards rejected
 * messages to an optional dead-letter channel.
 */
public class FilteringChannel implements MessageChannel {

    private final MessageChannel delegate;
    private final MessageFilter filter;
    private final MessageChannel deadLetter;
    private final List<Message> rejected = new CopyOnWriteArrayList<>();

    public FilteringChannel(MessageChannel delegate, MessageFilter filter) {
        this(delegate, filter, null);
    }

    public FilteringChannel(MessageChannel delegate, MessageFilter filter, MessageChannel deadLetter) {
        if (delegate == null || filter == null) {
            throw new IllegalArgumentException("delegate and filter must not be null");
        }
        this.delegate = delegate;
        this.filter = filter;
        this.deadLetter = deadLetter;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public void send(Message message) {
        if (filter.accept(message)) {
            delegate.send(message);
        } else {
            rejected.add(message);
            if (deadLetter != null) {
                deadLetter.send(message);
            }
        }
    }

    @Override
    public Message receive() {
        return delegate.receive();
    }

    @Override
    public Message receive(long timeoutMillis) {
        return delegate.receive(timeoutMillis);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void clear() {
        delegate.clear();
        rejected.clear();
    }

    @Override
    public List<Message> snapshot() {
        return delegate.snapshot();
    }

    public List<Message> rejected() {
        return new ArrayList<>(rejected);
    }
}