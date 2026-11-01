package com.patterns.saga;

/**
 * Lifecycle status of a saga execution.
 */
public enum SagaStatus {
    NOT_STARTED,
    STARTED,
    RUNNING,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}
