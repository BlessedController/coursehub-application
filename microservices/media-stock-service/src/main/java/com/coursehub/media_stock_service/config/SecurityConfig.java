package com.coursehub.media_stock_service.config;

import com.coursehub.commons.security.JwtAuthenticationFilter;
import com.coursehub.commons.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${services.identity.secret-key}")
    private String secretKey;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public JwtService jwtService() {
        return new JwtService(secretKey);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService(), handlerExceptionResolver);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/v1/media/photo/**").permitAll()
                        .requestMatchers("/v1/media/video/**").permitAll()
                        .anyRequest().permitAll());


        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        http.csrf(AbstractHttpConfigurer::disable);

        http.headers(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
