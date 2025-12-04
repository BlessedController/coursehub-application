package com.coursehub.commons.exceptions;

import com.coursehub.commons.exceptions.globals.GlobalExceptionMessage;

public class CustomFeignException extends RuntimeException {

    private GlobalExceptionMessage globalExceptionMessage;
    private String message;

    public CustomFeignException(GlobalExceptionMessage globalExceptionMessage) {
        this.globalExceptionMessage = globalExceptionMessage;
    }

    public CustomFeignException(String message) {
        this.message = message;
    }


    public GlobalExceptionMessage getGlobalExceptionMessage() {
        return this.globalExceptionMessage;
    }

    public String getMessage() {
        return this.message;
    }
}
