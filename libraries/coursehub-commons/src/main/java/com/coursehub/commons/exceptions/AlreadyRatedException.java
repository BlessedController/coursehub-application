package com.coursehub.commons.exceptions;

public class AlreadyRatedException extends RuntimeException {
    public AlreadyRatedException(String message) {
        super(message);
    }
}
