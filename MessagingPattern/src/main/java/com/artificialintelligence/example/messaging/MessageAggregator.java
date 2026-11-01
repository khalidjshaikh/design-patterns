package com.artificialintelligence.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Aggregates multiple related messages into a single message once a completion
 * condition is met.
 */
public class MessageAggregator {

    public interface CompletionStrategy {
        boolean isComplete(String correlationId, List<Message> messages);
    }

    private final int expectedCount;
    private final Map<String, List<Message>> groups = new ConcurrentHashMap<>();
    private final MessageChannel outputChannel;
    private final Function<List<Message>, Object> aggregator;

    public MessageAggregator(int expectedCount) {
        this(expectedCount, null, msgs -> new ArrayList<>(msgs));
    }

    public MessageAggregator(int expectedCount, MessageChannel outputChannel) {
        this(expectedCount, outputChannel, msgs -> new ArrayList<>(msgs));
    }

    public MessageAggregator(int expectedCount, MessageChannel outputChannel,
                            Function<List<Message>, Object> aggregator) {
        if (expectedCount <= 0) {
            throw new IllegalArgumentException("expectedCount must be positive");
        }
        this.expectedCount = expectedCount;
        this.outputChannel = outputChannel;
        this.aggregator = aggregator == null ? msgs -> new ArrayList<>(msgs) : aggregator;
    }

    public synchronized Message add(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        String key = message.correlationId() == null ? message.id() : message.correlationId();
        List<Message> group = groups.computeIfAbsent(key, k -> new ArrayList<>());
        group.add(message);
        if (group.size() >= expectedCount) {
            List<Message> completed = new ArrayList<>(group);
            groups.remove(key);
            Message aggregated = Message.builder()
                    .type("aggregated")
                    .payload(aggregator.apply(completed))
                    .correlationId(key)
                    .headers(message.headers())
                    .build();
            if (outputChannel != null) {
                outputChannel.send(aggregated);
            }
            return aggregated;
        }
        return null;
    }

    public int pendingGroups() {
        return groups.size();
    }

    public void clear() {
        groups.clear();
    }
}