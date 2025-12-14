package com.coursehub.course_service.config;

import com.coursehub.commons.security.filter.InternalCallFilter;
import com.coursehub.commons.security.filter.JwtAuthenticationFilter;
import com.coursehub.commons.security.service.JwtInternalTokenService;
import com.coursehub.commons.security.service.JwtUserAccessTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtUserAccessTokenService jwtServiceUserAccessToken;
    private final JwtInternalTokenService jwtServiceInternalToken;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtServiceUserAccessToken, handlerExceptionResolver);
    }

    @Bean
    public InternalCallFilter internalCallFilter() {
        return new InternalCallFilter(jwtServiceInternalToken, handlerExceptionResolver);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/api/courses/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/v1/api/courses/internal/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/courses/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/courses/videos/**").permitAll()
                        .requestMatchers("/v1/api/courses/creator/**").hasAnyRole("ADMIN", "CONTENT_CREATOR")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/courses/videos/**").hasAnyRole("CONTENT_CREATOR", "ADMIN")
                        .requestMatchers("/v1/api/courses/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/v1/api/courses/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/courses/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/api/courses/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/courses/categories/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalCallFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}
