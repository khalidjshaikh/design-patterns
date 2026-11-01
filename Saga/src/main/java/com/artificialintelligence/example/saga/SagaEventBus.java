package com.artificialintelligence.saga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Simple synchronous event bus used by choreographed sagas.
 */
public class SagaEventBus {

    private final Map<String, List<Consumer<SagaEvent>>> subscribers = new ConcurrentHashMap<>();

    public void subscribe(String eventType, Consumer<SagaEvent> handler) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(handler, "handler");
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    public void subscribeAll(Consumer<SagaEvent> handler) {
        subscribe("*", handler);
    }

    public void publish(SagaEvent event) {
        Objects.requireNonNull(event, "event");
        List<Consumer<SagaEvent>> direct = subscribers.get(event.getType());
        if (direct != null) {
            for (Consumer<SagaEvent> h : new ArrayList<>(direct)) {
                h.accept(event);
            }
        }
        List<Consumer<SagaEvent>> wildcard = subscribers.get("*");
        if (wildcard != null) {
            for (Consumer<SagaEvent> h : new ArrayList<>(wildcard)) {
                h.accept(event);
            }
        }
    }

    public void publish(String type, String sagaName, SagaContext context) {
        publish(new SagaEvent(type, sagaName, context));
    }
}
