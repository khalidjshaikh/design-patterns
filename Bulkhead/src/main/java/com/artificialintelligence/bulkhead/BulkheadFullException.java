package com.artificialintelligence.bulkhead;

/**
 * Thrown when a bulkhead has no capacity left to accept a new call.
 */
public class BulkheadFullException extends RuntimeException {

    private final String bulkheadName;

    public BulkheadFullException(String bulkheadName) {
        super("Bulkhead '" + bulkheadName + "' is full: no capacity available for a new call");
        this.bulkheadName = bulkheadName;
    }

    public String getBulkheadName() {
        return bulkheadName;
    }
}
