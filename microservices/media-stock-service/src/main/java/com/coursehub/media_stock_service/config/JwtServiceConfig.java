package com.coursehub.media_stock_service.config;

import com.coursehub.commons.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtServiceConfig {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Bean
    public JwtService jwtService() {
        return new JwtService(secretKey);
    }
}
