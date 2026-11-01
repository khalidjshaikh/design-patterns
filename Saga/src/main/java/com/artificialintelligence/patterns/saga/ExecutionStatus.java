package com.artificialintelligence.patterns.saga;

public enum ExecutionStatus {
    NOT_STARTED, STARTED, RUNNING, COMPLETED, COMPENSATING, COMPENSATED, FAILED
}
