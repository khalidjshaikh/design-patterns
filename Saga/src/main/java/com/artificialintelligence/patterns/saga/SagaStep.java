package com.artificialintelligence.patterns.saga;

/**
 * A single, compensable step of a saga.
 *
 * <p>Implementations should be idempotent where possible and must never throw
 * unchecked exceptions that escape {@link #execute(SagaContext)} without being
 * wrapped appropriately by the caller.</p>
 */
public interface SagaStep {

    String getName();

    void execute(SagaContext context) throws Exception;

    /**
     * Compensates (rolls back) the work performed by {@link #execute(SagaContext)}.
     * The default implementation is a no-op for steps that have nothing to undo.
     */
    default void compensate(SagaContext context) throws Exception {
        // no-op
    }

    // ------------------------------------------------------------------
    // Convenience aliases for alternative naming conventions.
    // ------------------------------------------------------------------
    default void action(SagaContext context) throws Exception {
        execute(context);
    }

    default void apply(SagaContext context) throws Exception {
        execute(context);
    }

    default void run(SagaContext context) throws Exception {
        execute(context);
    }

    default void rollback(SagaContext context) throws Exception {
        compensate(context);
    }

    default void undo(SagaContext context) throws Exception {
        compensate(context);
    }
}
