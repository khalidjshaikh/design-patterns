package com.artificialintelligence.bulkhead;

import java.util.function.Supplier;

/**
 * Bulkhead pattern: isolate failures by limiting the number of concurrent calls
 * that may be in flight at any given time.
 */
public interface Bulkhead {

    String getName();

    BulkheadMetrics getMetrics();

    <T> T executeSupplier(Supplier<T> supplier);

    void executeRunnable(Runnable runnable);

    /**
     * Attempts to acquire a permission to make a call. Returns {@code true} if
     * acquired, {@code false} otherwise.
     */
    boolean tryAcquirePermission();

    /**
     * Releases a previously acquired permission.
     */
    void releasePermission();

    int getAvailableConcurrentCalls();

    int getMaxAllowedConcurrentCalls();

    default <T> T execute(Supplier<T> supplier) {
        return executeSupplier(supplier);
    }

    default void execute(Runnable runnable) {
        executeRunnable(runnable);
    }

    default boolean tryAcquire() {
        return tryAcquirePermission();
    }

    default void release() {
        releasePermission();
    }
}
