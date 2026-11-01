package com.patterns.bulkhead;

/**
 * Snapshot of a bulkhead's capacity metrics.
 */
public interface BulkheadMetrics {

    int getAvailableConcurrentCalls();

    int getMaxAllowedConcurrentCalls();
}
