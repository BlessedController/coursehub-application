package com.coursehub.course_service.exception;

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

    private GlobalExceptionResponse createExceptionBody(HttpStatus status,
                                                        String message,
                                                        String path
    ) {

        ZonedDateTime bakuTime = ZonedDateTime.now(ZoneId.of("Asia/Baku"));
        String formattedTime = bakuTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return new GlobalExceptionResponse(
                formattedTime,
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> notFoundExceptionHandler(NotFoundException exception,
                                                                                  HttpServletRequest request) {

        GlobalExceptionResponse body = createExceptionBody(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalExceptionResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception,
                                                                                          HttpServletRequest request) {

        GlobalExceptionResponse body = createExceptionBody(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);

    }

    @ExceptionHandler(CategoryMismatchException.class)
    public ResponseEntity<GlobalExceptionResponse> categoryMismatchExceptionHandler(CategoryMismatchException exception,
                                                                                    HttpServletRequest request) {
        GlobalExceptionResponse body = createExceptionBody(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<GlobalExceptionResponse> authorIsNotTheOwnerOfTheCourseExceptionHandler(UnauthorizedOperationException exception,
                                                                                                  HttpServletRequest request) {
        GlobalExceptionResponse body = createExceptionBody(
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
