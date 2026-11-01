package com.artificialintelligence.patterns.saga;

public enum SagaState {
    NOT_STARTED, STARTED, RUNNING, COMPLETED, COMPENSATING, COMPENSATED, FAILED
}
