package com.coursehub.course_service.config;

import com.coursehub.commons.security.JwtService;
import com.coursehub.course_service.client.RetrieveMessageErrorDecoder;
import feign.Logger.Level;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.coursehub.commons.security.JwtService.*;
import static feign.Logger.Level.FULL;

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
            String internalToken = jwtService.generateInternalToken(COURSE_SERVICE_APP_NAME);

            template.header(INTERNAL_TOKEN_HEADER_NAME, "Bearer " + internalToken);
        };
    }


}
