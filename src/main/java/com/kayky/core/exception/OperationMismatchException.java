package com.kayky.core.exception;

public class OperationMismatchException extends RuntimeException {
    public OperationMismatchException(String message) {
        super(message);
    }
}