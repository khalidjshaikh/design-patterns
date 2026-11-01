package com.iluwatar.saga;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An event used by the choreography (event-driven) flavour of the saga pattern.
 */
public final class SagaEvent {

    private final String type;
    private final String sagaName;
    private final String sagaId;
    private final SagaContext context;
    private final Map<String, Object> payload;
    private final Instant timestamp;

    public SagaEvent(String type, String sagaName, SagaContext context) {
        this(type, sagaName, sagaName, context, new LinkedHashMap<>());
    }

    public SagaEvent(String type, String sagaName, String sagaId, SagaContext context, Map<String, Object> payload) {
        this.type = Objects.requireNonNull(type, "type");
        this.sagaName = sagaName;
        this.sagaId = sagaId;
        this.context = context;
        this.payload = payload == null ? new LinkedHashMap<>() : new LinkedHashMap<>(payload);
        this.timestamp = Instant.now();
    }

    public String getType() {
        return type;
    }

    public String getSagaName() {
        return sagaName;
    }

    public String getSagaId() {
        return sagaId;
    }

    public SagaContext getContext() {
        return context;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "SagaEvent{type=" + type + ", saga=" + sagaName + "}";
    }
}
