package com.coursehub.course_service.config;

import com.coursehub.commons.security.JwtAuthenticationFilter;
import com.coursehub.commons.security.JwtService;
import lombok.RequiredArgsConstructor;
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

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, handlerExceptionResolver);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/api/courses/public").permitAll()
                        .requestMatchers("/v1/api/courses/internal").permitAll()
                        .requestMatchers("/v1/api/courses/creator").hasAnyRole("ADMIN", "CONTENT_CREATOR")
                        .requestMatchers("/v1/api/courses/admin").hasRole("ADMIN")
                        .requestMatchers("/v1/api/courses/categories").hasRole("ADMIN")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
