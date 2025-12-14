package com.coursehub.media_stock_service.client;


import com.coursehub.commons.exceptions.CustomFeignException;
import com.coursehub.commons.exceptions.globals.GlobalExceptionMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        GlobalExceptionMessage message;

        HttpStatus httpStatus = HttpStatus.resolve(response.status());

        httpStatus = httpStatus == null ? INTERNAL_SERVER_ERROR : httpStatus;

        String exceptionMessage = "";

        String date = "";

        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {

            if (body != null) {
                String bodyContent = IOUtils.toString(body, UTF_8);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(bodyContent);
                exceptionMessage = root.get("exceptionMessage").asText();
            }

            if (response.headers().get("date") != null) {
                date = (String) response.headers().get("date").toArray()[0];
            }

            message = new GlobalExceptionMessage(
                    date,
                    response.status(),
                    httpStatus.getReasonPhrase(),
                    exceptionMessage,
                    response.request().url(),
                    null
            );


        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        return new CustomFeignException(message);
    }


}
