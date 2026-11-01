package com.artificialintelligence.saga;

/**
 * Thrown when a saga step fails during execution.
 */
public class SagaExecutionException extends SagaException {

    private final String stepName;

    public SagaExecutionException(String stepName, Throwable cause) {
        super("Saga step '" + stepName + "' failed", cause);
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }
}
