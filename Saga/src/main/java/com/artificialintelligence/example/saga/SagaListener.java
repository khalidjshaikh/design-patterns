package com.artificialintelligence.saga;

/**
 * Listener for saga lifecycle events.
 */
public interface SagaListener {

    default void onSagaStarted(String sagaName, SagaContext context) {
    }

    default void onStepStarted(String sagaName, String stepName, SagaContext context) {
    }

    default void onStepCompleted(String sagaName, String stepName, SagaContext context) {
    }

    default void onStepFailed(String sagaName, String stepName, SagaContext context, Throwable cause) {
    }

    default void onCompensationStarted(String sagaName, String stepName, SagaContext context) {
    }

    default void onCompensationCompleted(String sagaName, String stepName, SagaContext context) {
    }

    default void onCompensationFailed(String sagaName, String stepName, SagaContext context, Throwable cause) {
    }

    default void onSagaCompleted(String sagaName, SagaContext context, SagaResult result) {
    }
}
