package com.coursehub.payment_service.config;


import com.coursehub.commons.security.filter.InternalCallFilter;
import com.coursehub.commons.security.filter.JwtAuthenticationFilter;
import com.coursehub.commons.security.service.JwtInternalTokenService;
import com.coursehub.commons.security.service.JwtUserAccessTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/v1/api/payments/internal/**").permitAll()
                        .anyRequest().authenticated()
                )

                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalCallFilter(), JwtAuthenticationFilter.class);

        return http.build();

    }


}
