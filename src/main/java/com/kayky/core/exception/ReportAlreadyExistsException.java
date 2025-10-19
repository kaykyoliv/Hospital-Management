package com.kayky.core.exception;

public class ReportAlreadyExistsException extends RuntimeException {

    public ReportAlreadyExistsException(Long operationId) {
        super("Report already exists for operation with ID: " + operationId);
    }
}