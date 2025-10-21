package com.coursehub.course_service.exception;


public record GlobalExceptionResponse(
        String timestamp,
        int status,
        String reasonPhrase,
        String exceptionMessage,
        String path
) {
}

