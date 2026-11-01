package com.artificialintelligence.patterns.saga;

/**
 * Thrown when compensation of a saga step fails.
 */
public class CompensationException extends SagaException {

    private final String stepName;

    public CompensationException(String stepName, Throwable cause) {
        super("Compensation for saga step '" + stepName + "' failed", cause);
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }
}
