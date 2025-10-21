package com.coursehub.identity_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ZONE_ID = "Asia/Baku";

    private String getCurrentTimestamp() {
        return ZonedDateTime.now(ZoneId.of(ZONE_ID))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }


    private GenericExceptionResponse createBody(String message,
                                                String error,
                                                int status,
                                                String path
    ) {


        String timestamp = getCurrentTimestamp();

        return new GenericExceptionResponse(message, error, status, path, timestamp);

    }

    private ResponseEntity<GenericExceptionResponse> handleCustomException(RuntimeException exception,
                                                                           HttpStatus status,
                                                                           HttpServletRequest request
    ) {
        var body = createBody(exception.getMessage(),
                status.name(),
                status.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<GenericExceptionResponse> invalidRequestExceptionHandler(InvalidRequestException exception,
                                                                                   HttpServletRequest request) {
        return handleCustomException(exception, BAD_REQUEST, request);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<GenericExceptionResponse> userNotVerifiedExceptionHandler(UserNotVerifiedException exception,
                                                                                    HttpServletRequest request) {
        return handleCustomException(exception, FORBIDDEN, request);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<GenericExceptionResponse> userNotActiveExceptionHandler(UserNotActiveException exception,
                                                                                  HttpServletRequest request) {
        return handleCustomException(exception, FORBIDDEN, request);

    }

    @ExceptionHandler(ActivationMailCouldNotSendException.class)
    public ResponseEntity<GenericExceptionResponse> activationMailCouldNotSendExceptionHandler(
            ActivationMailCouldNotSendException exception,
            HttpServletRequest request) {
        return handleCustomException(exception, BAD_REQUEST, request);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GenericExceptionResponse> userNotFoundExceptionHandler(UserNotFoundException exception,
                                                                                 HttpServletRequest request) {
        return handleCustomException(exception, NOT_FOUND, request);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception,
                                                                    HttpServletRequest request) {

        Map<String, List<String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        String timestamp = getCurrentTimestamp();

        Map<String, Object> body = new HashMap<>();

        body.put("message", "validation failed");
        body.put("errors", errors);
        body.put("status", BAD_REQUEST.value());
        body.put("error", BAD_REQUEST.name());
        body.put("path", request.getRequestURI());
        body.put("timestamp", timestamp);


        return new ResponseEntity<>(body, BAD_REQUEST);
    }

}
