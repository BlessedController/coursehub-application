package com.coursehub.course_service.config;

import com.coursehub.course_service.client.RetrieveMessageErrorDecoder;
import feign.Logger.Level;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static feign.Logger.Level.FULL;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("Authorization");

                if (token != null) {
                    template.header("Authorization", token);
                } else {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if ("access_token".equals(cookie.getName())) {
                                String value = cookie.getValue();
                                if (value != null && !value.isEmpty()) {
                                    template.header("Authorization", "Bearer " + value);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }
}
