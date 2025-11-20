package com.coursehub.identity_service.security.config;

import com.coursehub.commons.security.JwtAuthenticationFilter;
import com.coursehub.identity_service.security.CustomOidcUserService;
import com.coursehub.identity_service.security.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/oauth2/**",                       // Google OAuth2 callback + authorization
                                "/oauth2/authorization/google",     // Google login start point
                                "/error",

                                // kendi auth endpointlerin
                                "/v1/api/users/auth/register",
                                "/v1/api/users/auth/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.oidcUserService(customOidcUserService))
                        .successHandler(successHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
