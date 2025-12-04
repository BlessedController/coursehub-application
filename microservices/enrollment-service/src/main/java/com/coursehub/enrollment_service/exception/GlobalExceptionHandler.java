package com.coursehub.enrollment_service.exception;

import com.coursehub.commons.exceptions.CustomFeignException;
import com.coursehub.commons.exceptions.InvalidRequestException;
import com.coursehub.commons.exceptions.globals.GlobalExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomFeignException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(CustomFeignException ex, HttpServletRequest request) {

        if (ex.getGlobalExceptionMessage() != null) {

            GlobalExceptionMessage msg = ex.getGlobalExceptionMessage();

            log.error("Feign error -> status: {}, url: {}, body: {}",
                    msg.statusCode(),
                    msg.uri(),
                    msg.reasonPhrase()
            );

            return status(msg.statusCode()).body(msg);
        } else {
            GlobalExceptionMessage message = GlobalExceptionMessage.builder()
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .statusCode(SERVICE_UNAVAILABLE.value())
                    .reasonPhrase(SERVICE_UNAVAILABLE.getReasonPhrase())
                    .uri(request.getRequestURI())
                    .exceptionMessage(ex.getMessage())
                    .validationErrors(null)
                    .build();

            return status(SERVICE_UNAVAILABLE).body(message);
        }
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<GlobalExceptionMessage> handle(InvalidRequestException ex, HttpServletRequest request) {

        GlobalExceptionMessage message = GlobalExceptionMessage.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .statusCode(BAD_REQUEST.value())
                .reasonPhrase(BAD_REQUEST.getReasonPhrase())
                .uri(request.getRequestURI())
                .exceptionMessage(ex.getMessage())
                .validationErrors(null)
                .build();

        return status(BAD_REQUEST).body(message);
    }

}
