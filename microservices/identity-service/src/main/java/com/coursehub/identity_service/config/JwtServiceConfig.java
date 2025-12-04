package com.coursehub.identity_service.config;

import com.coursehub.commons.security.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtServiceConfig {

    @Value("${jwt.user-secret-key}")
    private String userSecretKey;

    @Value("${jwt.internal-secret-key}")
    private String internalSecretKey;

    @Bean
    public JwtUserAccessTokenService jwtUserAccessTokenService() {
        return new JwtUserAccessTokenService(userSecretKey);
    }

    @Bean
    public JwtUserRefreshTokenService jwtUserRefreshTokenService() {
        return new JwtUserRefreshTokenService(userSecretKey);
    }

    @Bean
    public JwtInternalTokenService jwtInternalTokenService() {
        return new JwtInternalTokenService(internalSecretKey);
    }


}
