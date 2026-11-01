package com.artificialintelligence.patterns.saga;

public class SagaOutcome extends SagaResult {
    public SagaOutcome(boolean success, SagaStatus status) { super(success, status); }
    public SagaOutcome(boolean success, SagaStatus status, String failedStep) { super(success, status, failedStep); }
    public SagaOutcome(boolean success, SagaStatus status, String sagaName, String failedStep, Throwable cause) {
        super(success, status, sagaName, failedStep, cause);
    }
    public SagaOutcome(boolean success, SagaStatus status, String sagaName, String failedStep, Throwable cause,
                      int completedSteps, int totalSteps) {
        super(success, status, sagaName, failedStep, cause, completedSteps, totalSteps);
    }
}
