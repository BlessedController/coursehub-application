package com.coursehub.rating_service.config;

import com.coursehub.commons.security.JwtService;
import com.coursehub.rating_service.client.RetrieveMessageErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.coursehub.commons.security.JwtService.*;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final JwtService jwtService;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {

            String internalToken = jwtService.generateInternalToken(RATING_SERVICE_APP_NAME);

            template.header(INTERNAL_TOKEN_HEADER_NAME, "Bearer " + internalToken);
        };
    }

}
