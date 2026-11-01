package com.artificialintelligence.patterns.messaging;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable message exchanged over messaging channels.
 *
 * <p>A message carries an id, type, headers, payload, timestamp, correlation id and
 * reply-to channel name. Both {@code xxx()} (record-style) and {@code getXxx()}
 * accessors are provided for convenience.</p>
 */
public final class Message {

    private final String id;
    private final String type;
    private final Map<String, String> headers;
    private final Object payload;
    private final Instant timestamp;
    private final String correlationId;
    private final String replyTo;

    private Message(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(builder.headers));
        this.payload = builder.payload;
        this.timestamp = builder.timestamp;
        this.correlationId = builder.correlationId;
        this.replyTo = builder.replyTo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Message of(Object payload) {
        return builder().payload(payload).build();
    }

    public static Message of(String type, Object payload) {
        return builder().type(type).payload(payload).build();
    }

    public static Message of(String type, Object payload, Map<String, String> headers) {
        return builder().type(type).payload(payload).headers(headers).build();
    }

    // ---- record-style accessors ----
    public String id() { return id; }
    public String type() { return type; }
    public Map<String, String> headers() { return headers; }
    public Object payload() { return payload; }
    public Instant timestamp() { return timestamp; }
    public String correlationId() { return correlationId; }
    public String replyTo() { return replyTo; }

    // ---- JavaBean-style accessors ----
    public String getId() { return id; }
    public String getType() { return type; }
    public Map<String, String> getHeaders() { return headers; }
    public Object getPayload() { return payload; }
    public Instant getTimestamp() { return timestamp; }
    public String getCorrelationId() { return correlationId; }
    public String getReplyTo() { return replyTo; }

    @SuppressWarnings("unchecked")
    public <T> T payloadAs(Class<T> type) {
        return (T) payload;
    }

    public String header(String name) {
        return headers.get(name);
    }

    public Message withCorrelationId(String correlationId) {
        return builder().id(id).type(type).headers(headers).payload(payload)
                .timestamp(timestamp).correlationId(correlationId).replyTo(replyTo).build();
    }

    public Message withReplyTo(String replyTo) {
        return builder().id(id).type(type).headers(headers).payload(payload)
                .timestamp(timestamp).correlationId(correlationId).replyTo(replyTo).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message that = (Message) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{id='" + id + "', type='" + type + "', payload=" + payload + "}";
    }

    public static final class Builder {
        private String id = UUID.randomUUID().toString();
        private String type = "text";
        private final Map<String, String> headers = new LinkedHashMap<>();
        private Object payload;
        private Instant timestamp = Instant.now();
        private String correlationId;
        private String replyTo;

        public Builder id(String id) {
            if (id != null) this.id = id;
            return this;
        }

        public Builder type(String type) {
            if (type != null) this.type = type;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.clear();
            if (headers != null) this.headers.putAll(headers);
            return this;
        }

        public Builder header(String name, String value) {
            if (name != null) this.headers.put(name, value);
            return this;
        }

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            if (timestamp != null) this.timestamp = timestamp;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}