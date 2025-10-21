package com.coursehub.course_service.client;


import com.coursehub.course_service.exception.GlobalExceptionResponse;
import com.coursehub.course_service.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class RetrieveMessageErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        GlobalExceptionResponse message = null;

        HttpStatus httpStatus = HttpStatus.resolve(response.status());

        httpStatus = httpStatus == null ? INTERNAL_SERVER_ERROR : httpStatus;

        String bodyContent = "";

        String date = "";

        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {

            if (body != null) {
                bodyContent = IOUtils.toString(body, UTF_8);
            }

            if (response.headers().get("date") != null) {
                date = (String) response.headers().get("date").toArray()[0];
            }

            message = new GlobalExceptionResponse(
                    date,
                    response.status(),
                    httpStatus.getReasonPhrase(),
                    bodyContent,
                    response.request().url()
            );


        } catch (IOException e) {
            return new Exception(e.getMessage());
        }


        if (response.status() == 404) {
            return new NotFoundException(message);
        }

        return defaultDecoder.decode(methodKey, response);
    }


}
