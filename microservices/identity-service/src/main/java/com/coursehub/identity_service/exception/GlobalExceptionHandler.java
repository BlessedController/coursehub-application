package com.coursehub.identity_service.exception;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.globals.GlobalExceptionMessage;
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

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<GlobalExceptionMessage> invalidRequestExceptionHandler(InvalidRequestException exception,
                                                                                 HttpServletRequest request) {

        var body = createExceptionBody(BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, BAD_REQUEST);

    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<GlobalExceptionMessage> userNotVerifiedExceptionHandler(UserNotVerifiedException exception,
                                                                                  HttpServletRequest request) {
        var body = createExceptionBody(FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, FORBIDDEN);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<GlobalExceptionMessage> userNotActiveExceptionHandler(UserNotActiveException exception,
                                                                                HttpServletRequest request) {
        var body = createExceptionBody(FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, FORBIDDEN);
    }

    @ExceptionHandler(ActivationMailCouldNotSendException.class)
    public ResponseEntity<GlobalExceptionMessage> activationMailCouldNotSendExceptionHandler(
            ActivationMailCouldNotSendException exception,
            HttpServletRequest request) {
        var body = createExceptionBody(BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, BAD_REQUEST);
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalExceptionMessage> userNotFoundExceptionHandler(NotFoundException exception,
                                                                               HttpServletRequest request) {
        var body = createExceptionBody(NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, NOT_FOUND);
    }

    // TODO: HttpMethodNotArgumentException

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception,
                                                                    HttpServletRequest request) {

        Map<String, List<String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        ZonedDateTime bakuTime = ZonedDateTime.now(ZoneId.of(ZONE_ID));
        String formattedTime = bakuTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Map<String, Object> body = new HashMap<>();

        body.put("message", "validation failed");
        body.put("errors", errors);
        body.put("status", BAD_REQUEST.value());
        body.put("error", BAD_REQUEST.name());
        body.put("path", request.getRequestURI());
        body.put("timestamp", formattedTime);


        return new ResponseEntity<>(body, BAD_REQUEST);
    }

}
