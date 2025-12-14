package com.coursehub.identity_service.exception;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.exceptions.globals.GlobalExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.time.*;
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
    public ResponseEntity<GlobalExceptionMessage> handle(InvalidRequestException exception,
                                                         HttpServletRequest request) {

        var body = createExceptionBody(BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, BAD_REQUEST);

    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(LoginException exception,
                                                         HttpServletRequest request) {
        var body = createExceptionBody(UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(UserNotVerifiedException exception,
                                                         HttpServletRequest request) {
        var body = createExceptionBody(FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, FORBIDDEN);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(UserNotActiveException exception,
                                                         HttpServletRequest request) {
        var body = createExceptionBody(FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, FORBIDDEN);
    }

    @ExceptionHandler(ActivationMailCouldNotSendException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(
            ActivationMailCouldNotSendException exception,
            HttpServletRequest request) {
        var body = createExceptionBody(BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(ConflictException exception,
                                                         HttpServletRequest request) {
        var body = createExceptionBody(CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(NotFoundException exception,
                                                         HttpServletRequest request) {
        var body = createExceptionBody(NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, NOT_FOUND);
    }

    // TODO: Handle HttpMethodNotArgumentException


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handle(AuthenticationException exception,
                                    HttpServletRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "UNAUTHORIZED");
        body.put("message", exception.getMessage());
        body.put("path", request.getRequestURI());
        body.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity
                .status(HttpServletResponse.SC_UNAUTHORIZED) // 401
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException exception,
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
