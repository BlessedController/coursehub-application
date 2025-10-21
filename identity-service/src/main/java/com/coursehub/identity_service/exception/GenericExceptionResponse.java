package com.coursehub.identity_service.exception;

public record GenericExceptionResponse(
        String message,
        String error,
        int status,
        String path,
        String timestamp) {
}
