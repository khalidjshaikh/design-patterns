package com.artificialintelligence.saga;

public class SagaExecutionResult extends SagaResult {
    public SagaExecutionResult(boolean success, SagaStatus status) { super(success, status); }
    public SagaExecutionResult(boolean success, SagaStatus status, String failedStep) { super(success, status, failedStep); }
    public SagaExecutionResult(boolean success, SagaStatus status, String sagaName, String failedStep, Throwable cause) {
        super(success, status, sagaName, failedStep, cause);
    }
    public SagaExecutionResult(boolean success, SagaStatus status, String sagaName, String failedStep, Throwable cause,
                              int completedSteps, int totalSteps) {
        super(success, status, sagaName, failedStep, cause, completedSteps, totalSteps);
    }
}
