package com.coursehub.identity_service.security.config;

import com.coursehub.commons.security.JwtAuthenticationFilter;
import com.coursehub.commons.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilterConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, handlerExceptionResolver);
    }

}
