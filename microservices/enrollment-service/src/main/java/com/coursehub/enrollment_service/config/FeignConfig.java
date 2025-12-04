package com.coursehub.enrollment_service.config;

import com.coursehub.commons.security.service.JwtInternalTokenService;
import com.coursehub.enrollment_service.client.RetrieveMessageErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.coursehub.commons.security.constants.JwtConstants.ENROLLMENT_SERVICE_APP_NAME;
import static com.coursehub.commons.security.constants.JwtConstants.INTERNAL_TOKEN_HEADER_NAME;


@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final JwtInternalTokenService jwtServiceInternalToken;


    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String internalToken = jwtServiceInternalToken.generateInternalToken(ENROLLMENT_SERVICE_APP_NAME);

            template.header(INTERNAL_TOKEN_HEADER_NAME, "Bearer " + internalToken);
        };
    }

}
