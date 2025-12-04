package com.coursehub.media_stock_service.exception;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.exceptions.globals.GlobalExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
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
