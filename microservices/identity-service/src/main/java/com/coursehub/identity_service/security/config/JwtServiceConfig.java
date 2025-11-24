package com.coursehub.identity_service.security.config;

import com.coursehub.commons.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtServiceConfig {

    @Value("${identity-service.jwt.user-secret-key}")
    private String secretKey;

    @Bean
    public JwtService jwtService() {
        return new JwtService(secretKey);
    }

}
