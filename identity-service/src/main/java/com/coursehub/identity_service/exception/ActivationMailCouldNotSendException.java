package com.coursehub.identity_service.exception;

public class ActivationMailCouldNotSendException extends RuntimeException {
    public ActivationMailCouldNotSendException(String message) {
        super(message);
    }
}
