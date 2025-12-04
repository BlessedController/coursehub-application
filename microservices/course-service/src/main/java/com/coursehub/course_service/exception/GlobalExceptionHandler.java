package com.coursehub.course_service.exception;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.exceptions.globals.GlobalExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<GlobalExceptionMessage> notFoundExceptionHandler(NotFoundException exception,
                                                                           HttpServletRequest request) {

        GlobalExceptionMessage body = createExceptionBody(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalExceptionMessage> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception,
                                                                                         HttpServletRequest request) {

        GlobalExceptionMessage body = createExceptionBody(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);

    }

    @ExceptionHandler(CategoryMismatchException.class)
    public ResponseEntity<GlobalExceptionMessage> categoryMismatchExceptionHandler(CategoryMismatchException exception,
                                                                                   HttpServletRequest request) {
        GlobalExceptionMessage body = createExceptionBody(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<GlobalExceptionMessage> authorIsNotTheOwnerOfTheCourseExceptionHandler(UnauthorizedOperationException exception,
                                                                                                 HttpServletRequest request) {
        GlobalExceptionMessage body = createExceptionBody(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {

        Map<String, List<String>> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

}
