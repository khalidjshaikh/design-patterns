package com.artificialintelligence.circuitbreaker;

import java.time.Duration;

/**
 * Configuration for a {@link CircuitBreaker}.
 */
public final class CircuitBreakerConfig {

    public static final int DEFAULT_FAILURE_THRESHOLD = 5;
    public static final Duration DEFAULT_WAIT_DURATION = Duration.ofSeconds(60);
    public static final int DEFAULT_PERMITTED_CALLS_IN_HALF_OPEN = 1;

    private final int failureThreshold;
    private final Duration waitDuration;
    private final int permittedNumberOfCallsInHalfOpenState;

    private CircuitBreakerConfig(Builder builder) {
        this.failureThreshold = builder.failureThreshold;
        this.waitDuration = builder.waitDuration;
        this.permittedNumberOfCallsInHalfOpenState = builder.permittedNumberOfCallsInHalfOpenState;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static CircuitBreakerConfig ofDefaults() {
        return builder().build();
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public int getFailureRateThreshold() {
        return failureThreshold;
    }

    public Duration getWaitDuration() {
        return waitDuration;
    }

    public Duration getWaitDurationInOpenState() {
        return waitDuration;
    }

    public long getWaitDurationInOpenStateInMillis() {
        return waitDuration.toMillis();
    }

    public long getResetTimeoutMs() {
        return waitDuration.toMillis();
    }

    public int getPermittedNumberOfCallsInHalfOpenState() {
        return permittedNumberOfCallsInHalfOpenState;
    }

    public static final class Builder {
        private int failureThreshold = DEFAULT_FAILURE_THRESHOLD;
        private Duration waitDuration = DEFAULT_WAIT_DURATION;
        private int permittedNumberOfCallsInHalfOpenState = DEFAULT_PERMITTED_CALLS_IN_HALF_OPEN;

        public Builder failureThreshold(int failureThreshold) {
            if (failureThreshold <= 0) {
                throw new IllegalArgumentException("failureThreshold must be positive");
            }
            this.failureThreshold = failureThreshold;
            return this;
        }

        public Builder failureRateThreshold(int failureRateThreshold) {
            return failureThreshold(failureRateThreshold);
        }

        public Builder waitDuration(Duration waitDuration) {
            if (waitDuration == null || waitDuration.isNegative()) {
                throw new IllegalArgumentException("waitDuration must be non-negative");
            }
            this.waitDuration = waitDuration;
            return this;
        }

        public Builder waitDurationInOpenState(Duration waitDurationInOpenState) {
            return waitDuration(waitDurationInOpenState);
        }

        public Builder resetTimeoutMs(long resetTimeoutMs) {
            return waitDuration(Duration.ofMillis(resetTimeoutMs));
        }

        public Builder waitDurationInMillis(long waitDurationInMillis) {
            return waitDuration(Duration.ofMillis(waitDurationInMillis));
        }

        public Builder permittedNumberOfCallsInHalfOpenState(int permittedNumberOfCallsInHalfOpenState) {
            if (permittedNumberOfCallsInHalfOpenState <= 0) {
                throw new IllegalArgumentException("permittedNumberOfCallsInHalfOpenState must be positive");
            }
            this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
            return this;
        }

        public CircuitBreakerConfig build() {
            return new CircuitBreakerConfig(this);
        }
    }
}