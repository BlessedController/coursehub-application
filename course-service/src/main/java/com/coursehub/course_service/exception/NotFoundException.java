package com.coursehub.course_service.exception;

public class NotFoundException extends RuntimeException {
    private  GlobalExceptionResponse globalExceptionResponse;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(GlobalExceptionResponse globalExceptionResponse) {
        this.globalExceptionResponse = globalExceptionResponse;
    }
}
