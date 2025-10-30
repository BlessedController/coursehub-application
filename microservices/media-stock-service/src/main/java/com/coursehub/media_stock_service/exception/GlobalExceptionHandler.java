package com.coursehub.media_stock_service.exception;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.globals.GlobalExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(AccessDeniedException exception,
                                                         HttpServletRequest request) {

        GlobalExceptionMessage body = createExceptionBody(
                FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, FORBIDDEN);
    }


    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(FileOperationException exception,
                                                         HttpServletRequest request) {

        GlobalExceptionMessage body = createExceptionBody(BAD_REQUEST, exception.getMessage(), request.getRequestURI());

        return new ResponseEntity<>(body, BAD_REQUEST);
    }


    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(InvalidFileFormatException exception,
                                                         HttpServletRequest request) {

        GlobalExceptionMessage body = createExceptionBody(BAD_REQUEST, exception.getMessage(), request.getRequestURI());

        return new ResponseEntity<>(body, BAD_REQUEST);
    }

}
