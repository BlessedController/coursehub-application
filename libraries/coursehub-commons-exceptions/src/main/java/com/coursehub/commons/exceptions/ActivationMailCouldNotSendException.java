package com.coursehub.commons.exceptions;

public class ActivationMailCouldNotSendException extends RuntimeException {
    public ActivationMailCouldNotSendException(String message) {
        super(message);
    }
}
