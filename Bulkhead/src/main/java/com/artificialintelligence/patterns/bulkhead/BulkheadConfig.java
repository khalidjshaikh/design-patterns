package com.artificialintelligence.patterns.bulkhead;

import java.time.Duration;

/**
 * Configuration for a {@link Bulkhead}.
 */
public final class BulkheadConfig {

    public static final int DEFAULT_MAX_CONCURRENT_CALLS = 10;
    public static final Duration DEFAULT_MAX_WAIT_DURATION = Duration.ofMillis(0);
    public static final int DEFAULT_CORE_THREAD_POOL_SIZE = 2;
    public static final int DEFAULT_MAX_THREAD_POOL_SIZE = 4;
    public static final int DEFAULT_QUEUE_CAPACITY = 5;
    public static final Duration DEFAULT_KEEP_ALIVE_DURATION = Duration.ofSeconds(20);

    private final int maxConcurrentCalls;
    private final Duration maxWaitDuration;

    private final int coreThreadPoolSize;
    private final int maxThreadPoolSize;
    private final int queueCapacity;
    private final Duration keepAliveDuration;

    private BulkheadConfig(Builder builder) {
        this.maxConcurrentCalls = builder.maxConcurrentCalls;
        this.maxWaitDuration = builder.maxWaitDuration;
        this.coreThreadPoolSize = builder.coreThreadPoolSize;
        this.maxThreadPoolSize = builder.maxThreadPoolSize;
        this.queueCapacity = builder.queueCapacity;
        this.keepAliveDuration = builder.keepAliveDuration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static BulkheadConfig ofDefaults() {
        return builder().build();
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public Duration getMaxWaitDuration() {
        return maxWaitDuration;
    }

    public int getCoreThreadPoolSize() {
        return coreThreadPoolSize;
    }

    public int getMaxThreadPoolSize() {
        return maxThreadPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public Duration getKeepAliveDuration() {
        return keepAliveDuration;
    }

    public static final class Builder {
        private int maxConcurrentCalls = DEFAULT_MAX_CONCURRENT_CALLS;
        private Duration maxWaitDuration = DEFAULT_MAX_WAIT_DURATION;
        private int coreThreadPoolSize = DEFAULT_CORE_THREAD_POOL_SIZE;
        private int maxThreadPoolSize = DEFAULT_MAX_THREAD_POOL_SIZE;
        private int queueCapacity = DEFAULT_QUEUE_CAPACITY;
        private Duration keepAliveDuration = DEFAULT_KEEP_ALIVE_DURATION;

        public Builder maxConcurrentCalls(int maxConcurrentCalls) {
            if (maxConcurrentCalls <= 0) {
                throw new IllegalArgumentException("maxConcurrentCalls must be positive");
            }
            this.maxConcurrentCalls = maxConcurrentCalls;
            return this;
        }

        public Builder maxWaitDuration(Duration maxWaitDuration) {
            if (maxWaitDuration == null || maxWaitDuration.isNegative()) {
                throw new IllegalArgumentException("maxWaitDuration must be non-negative");
            }
            this.maxWaitDuration = maxWaitDuration;
            return this;
        }

        public Builder coreThreadPoolSize(int coreThreadPoolSize) {
            if (coreThreadPoolSize <= 0) {
                throw new IllegalArgumentException("coreThreadPoolSize must be positive");
            }
            this.coreThreadPoolSize = coreThreadPoolSize;
            return this;
        }

        public Builder maxThreadPoolSize(int maxThreadPoolSize) {
            if (maxThreadPoolSize <= 0) {
                throw new IllegalArgumentException("maxThreadPoolSize must be positive");
            }
            this.maxThreadPoolSize = maxThreadPoolSize;
            return this;
        }

        public Builder queueCapacity(int queueCapacity) {
            if (queueCapacity < 0) {
                throw new IllegalArgumentException("queueCapacity must be non-negative");
            }
            this.queueCapacity = queueCapacity;
            return this;
        }

        public Builder keepAliveDuration(Duration keepAliveDuration) {
            if (keepAliveDuration == null || keepAliveDuration.isNegative()) {
                throw new IllegalArgumentException("keepAliveDuration must be non-negative");
            }
            this.keepAliveDuration = keepAliveDuration;
            return this;
        }

        public BulkheadConfig build() {
            if (maxThreadPoolSize < coreThreadPoolSize) {
                throw new IllegalArgumentException("maxThreadPoolSize must be >= coreThreadPoolSize");
            }
            return new BulkheadConfig(this);
        }
    }
}
