package com.coursehub.course_service.config;

import com.coursehub.commons.security.service.JwtInternalTokenService;
import com.coursehub.course_service.client.RetrieveMessageErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.coursehub.commons.security.constants.JwtConstants.COURSE_SERVICE_APP_NAME;
import static com.coursehub.commons.security.constants.JwtConstants.INTERNAL_TOKEN_HEADER_NAME;

@Slf4j
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
            String internalToken = jwtServiceInternalToken.generateInternalToken(COURSE_SERVICE_APP_NAME);

            template.header(INTERNAL_TOKEN_HEADER_NAME, "Bearer " + internalToken);
        };
    }


}
