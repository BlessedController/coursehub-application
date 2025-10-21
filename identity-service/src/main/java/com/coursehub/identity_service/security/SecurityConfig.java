package com.coursehub.identity_service.security;

import com.coursehub.identity_service.exception.AuthEntryPoint;
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
public class SecurityConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(HandlerExceptionResolver handlerExceptionResolver, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/v1/user/register").permitAll()
                        .requestMatchers("/v1/user/login").permitAll()
                        .requestMatchers("/v1/user/refresh").permitAll()
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/v1/user/**").authenticated()
                        .anyRequest().authenticated());

        http.httpBasic(httpBasic ->
                httpBasic.authenticationEntryPoint(new AuthEntryPoint(handlerExceptionResolver))
        );

        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
