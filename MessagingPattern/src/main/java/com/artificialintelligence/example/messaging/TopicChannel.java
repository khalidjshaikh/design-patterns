package com.artificialintelligence.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Publish/subscribe channel: every published message is delivered to all subscribers.
 */
public class TopicChannel implements MessageChannel {

    /** Backwards-compatible alias for {@link com.artificialintelligence.messaging.MessageListener}. */
    public interface MessageListener extends com.artificialintelligence.messaging.MessageListener {
    }

    private final String name;
    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private final List<com.artificialintelligence.messaging.MessageListener> listeners = new CopyOnWriteArrayList<>();
    private final List<BlockingQueue<Message>> subscriberQueues = new CopyOnWriteArrayList<>();

    public TopicChannel(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    /** Registers a push subscriber. */
    public void subscribe(com.artificialintelligence.messaging.MessageListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        listeners.add(listener);
    }

    /** Removes a push subscriber. */
    public void unsubscribe(MessageListener listener) {
        listeners.remove(listener);
    }

    /** Creates a pull subscription channel that receives published messages. */
    public MessageChannel subscribeChannel() {
        BlockingQueue<Message> q = new LinkedBlockingQueue<>();
        subscriberQueues.add(q);
        return new SubscriptionChannel(name + ".sub", q, subscriberQueues);
    }

    /** Alias for {@link #subscribeChannel()}. */
    public MessageChannel subscribe() {
        return subscribeChannel();
    }

    @Override
    public void send(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        messages.offer(message);
        for (com.artificialintelligence.messaging.MessageListener listener : listeners) {
            try {
                listener.onMessage(message);
            } catch (RuntimeException ignored) {
                // Listeners must not break publishing.
            }
        }
        for (BlockingQueue<Message> q : subscriberQueues) {
            q.offer(message);
        }
    }

    /** Alias for publish. */
    public void publish(Message message) {
        send(message);
    }

    @Override
    public Message receive() {
        try {
            return messages.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public Message receive(long timeoutMillis) {
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("timeoutMillis must be non-negative");
        }
        try {
            return messages.poll(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public int size() {
        return messages.size();
    }

    @Override
    public boolean isEmpty() {
        return messages.isEmpty();
    }

    @Override
    public void clear() {
        messages.clear();
    }

    @Override
    public List<Message> snapshot() {
        return new ArrayList<>(messages);
    }

    public int subscriberCount() {
        return listeners.size() + subscriberQueues.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TopicChannel)) return false;
        TopicChannel that = (TopicChannel) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "TopicChannel{name='" + name + "', size=" + size() + "}";
    }

    private static final class SubscriptionChannel implements MessageChannel {
        private final String name;
        private final BlockingQueue<Message> queue;
        private final List<BlockingQueue<Message>> owner;

        SubscriptionChannel(String name, BlockingQueue<Message> queue, List<BlockingQueue<Message>> owner) {
            this.name = name;
            this.queue = queue;
            this.owner = owner;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void send(Message message) {
            // Publishing to a subscription is not supported; publish to the parent topic instead.
            throw new UnsupportedOperationException("Cannot send directly to a subscription channel");
        }

        @Override
        public Message receive() {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        @Override
        public Message receive(long timeoutMillis) {
            if (timeoutMillis < 0) {
                throw new IllegalArgumentException("timeoutMillis must be non-negative");
            }
            try {
                return queue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        @Override
        public int size() {
            return queue.size();
        }

        @Override
        public boolean isEmpty() {
            return queue.isEmpty();
        }

        @Override
        public void clear() {
            queue.clear();
        }

        @Override
        public List<Message> snapshot() {
            return new ArrayList<>(queue);
        }
    }
}