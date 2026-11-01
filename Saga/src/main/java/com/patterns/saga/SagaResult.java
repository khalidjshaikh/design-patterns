package com.patterns.saga;

import java.util.Objects;

/**
 * Result of a saga execution.
 */
public class SagaResult {

    private final boolean success;
    private final SagaStatus status;
    private final String sagaName;
    private final String failedStep;
    private final Throwable cause;
    private final int completedSteps;
    private final int totalSteps;

    public SagaResult(boolean success, SagaStatus status) {
        this(success, status, null, null, null, 0, 0);
    }

    public SagaResult(boolean success, SagaStatus status, String failedStep) {
        this(success, status, null, failedStep, null, 0, 0);
    }

    public SagaResult(boolean success, SagaStatus status, String sagaName, String failedStep, Throwable cause) {
        this(success, status, sagaName, failedStep, cause, 0, 0);
    }

    public SagaResult(boolean success, SagaStatus status, String sagaName, String failedStep, Throwable cause,
                     int completedSteps, int totalSteps) {
        this.success = success;
        this.status = Objects.requireNonNull(status, "status");
        this.sagaName = sagaName;
        this.failedStep = failedStep;
        this.cause = cause;
        this.completedSteps = completedSteps;
        this.totalSteps = totalSteps;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public String getSagaName() {
        return sagaName;
    }

    public String getFailedStep() {
        return failedStep;
    }

    public Throwable getCause() {
        return cause;
    }

    public int getCompletedSteps() {
        return completedSteps;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    @Override
    public String toString() {
        return "SagaResult{success=" + success + ", status=" + status
                + ", sagaName=" + sagaName + ", failedStep=" + failedStep
                + ", completedSteps=" + completedSteps + "/" + totalSteps + "}";
    }
}
