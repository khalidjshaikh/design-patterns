package com.artificialintelligence.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory store for messages.
 */
public class MessageStore {

    private final Map<String, Message> store = new ConcurrentHashMap<>();

    public void save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        store.put(message.id(), message);
    }

    public Message get(String id) {
        return store.get(id);
    }

    public boolean contains(String id) {
        return store.containsKey(id);
    }

    public Message remove(String id) {
        return store.remove(id);
    }

    public List<Message> all() {
        return new ArrayList<>(store.values());
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}