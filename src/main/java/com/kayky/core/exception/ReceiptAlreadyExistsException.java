package com.kayky.core.exception;

public class ReceiptAlreadyExistsException extends RuntimeException {

    public ReceiptAlreadyExistsException(Long paymentId) {
        super("Receipt already exists for payment id " + paymentId);
    }
}