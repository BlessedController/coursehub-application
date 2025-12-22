package com.coursehub.identity_service.config;

import com.coursehub.commons.security.filter.InternalCallFilter;
import com.coursehub.commons.security.filter.JwtAuthenticationFilter;
import com.coursehub.commons.security.service.JwtInternalTokenService;
import com.coursehub.commons.security.service.JwtUserAccessTokenService;
import com.coursehub.identity_service.security.CustomOidcUserService;
import com.coursehub.identity_service.security.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final AuthenticationEntryPoint authEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtInternalTokenService jwtInternalTokenService;
    private final JwtUserAccessTokenService jwtServiceUserAccessToken;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtServiceUserAccessToken, handlerExceptionResolver);
    }

    @Bean
    public InternalCallFilter internalCallFilter() {
        return new InternalCallFilter(jwtInternalTokenService, handlerExceptionResolver);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors->cors.configurationSource(corsConfigurationSource))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/oauth2/**",
                                "/oauth2/authorization/google",
                                "/error",
                                "/v1/api/users/auth/register",
                                "/v1/api/users/auth/login",
                                "/v1/api/users/auth/refresh",
                                "/v1/api/users/auth/forgot-password",
                                "/v1/api/users/auth/reset-forgotten-password",
                                "/v1/api/users/auth/verify/**",
                                "/v1/api/users/public/**",
                                "/v1/api/users/internal/**"
                        ).permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/v1/api/users/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.oidcUserService(customOidcUserService))
                        .successHandler(successHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalCallFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

}
