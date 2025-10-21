package com.coursehub.media_stock_service.exception;

public record GlobalExceptionMessage(
        String timestamp,
        int status,
        String reasonPhrase,
        String exceptionMessage,
        String path
) {
}
