package com.patterns.saga;

public class SagaFailedException extends SagaException {
    public SagaFailedException(String message) { super(message); }
    public SagaFailedException(String message, Throwable cause) { super(message, cause); }
}
