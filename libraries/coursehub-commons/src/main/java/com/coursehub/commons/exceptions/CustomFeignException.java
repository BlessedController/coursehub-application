package com.coursehub.commons.exceptions;

import com.coursehub.commons.globals.GlobalExceptionMessage;

public class CustomFeignException extends RuntimeException {

    private GlobalExceptionMessage globalExceptionMessage;

    public CustomFeignException(GlobalExceptionMessage globalExceptionMessage) {
        this.globalExceptionMessage = globalExceptionMessage;
    }


}
