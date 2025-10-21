package com.coursehub.rating_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String ZONE_ID = "Asia/Baku";

    private String getCurrentTimestamp() {
        return ZonedDateTime.now(ZoneId.of(ZONE_ID))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }


    private ExceptionMessage createBody(int status,
                                        String reasonPhrase,
                                        String path,
                                        String message

    ) {
        String timestamp = getCurrentTimestamp();

        return ExceptionMessage.builder()
                .timestamp(timestamp)
                .status(status)
                .reasonPhrase(reasonPhrase)
                .path(path)
                .message(message)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionMessage> handle(NotFoundException e, HttpServletRequest request) {

        ExceptionMessage message = createBody(
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                request.getRequestURI(),
                e.getMessage()
        );

        return status(NOT_FOUND).body(message);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionMessage> handle(AccessDeniedException e, HttpServletRequest request) {

        ExceptionMessage message = createBody(
                FORBIDDEN.value(),
                FORBIDDEN.getReasonPhrase(),
                request.getRequestURI(),
                e.getMessage()
        );

        return status(FORBIDDEN).body(message);
    }


}
