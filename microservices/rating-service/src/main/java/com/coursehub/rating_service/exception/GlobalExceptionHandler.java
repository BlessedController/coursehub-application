package com.coursehub.rating_service.exception;

import com.coursehub.commons.exceptions.AccessDeniedException;
import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.globals.GlobalExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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

    private GlobalExceptionMessage createExceptionBody(HttpStatus status,
                                                       String message,
                                                       String uri
    ) {

        ZonedDateTime bakuTime = ZonedDateTime.now(ZoneId.of(ZONE_ID));
        String formattedTime = bakuTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return GlobalExceptionMessage.builder()
                .timestamp(formattedTime)
                .statusCode(status.value())
                .reasonPhrase(status.getReasonPhrase())
                .exceptionMessage(message)
                .uri(uri)
                .build();

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(NotFoundException e, HttpServletRequest request) {

        GlobalExceptionMessage message = createExceptionBody(NOT_FOUND, e.getMessage(), request.getRequestURI());

        return status(NOT_FOUND).body(message);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(AccessDeniedException e, HttpServletRequest request) {

        GlobalExceptionMessage message = createExceptionBody(FORBIDDEN, e.getMessage(), request.getRequestURI());

        return status(FORBIDDEN).body(message);
    }


}
