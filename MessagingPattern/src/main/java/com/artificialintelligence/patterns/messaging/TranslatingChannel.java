package com.artificialintelligence.patterns.messaging;

import java.util.List;

/**
 * A channel wrapper that translates messages before forwarding them.
 */
public class TranslatingChannel implements MessageChannel {

    private final MessageChannel delegate;
    private final MessageTranslator translator;

    public TranslatingChannel(MessageChannel delegate, MessageTranslator translator) {
        if (delegate == null || translator == null) {
            throw new IllegalArgumentException("delegate and translator must not be null");
        }
        this.delegate = delegate;
        this.translator = translator;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public void send(Message message) {
        Message translated = translator.translate(message);
        delegate.send(translated == null ? message : translated);
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
    }

    @Override
    public List<Message> snapshot() {
        return delegate.snapshot();
    }
}