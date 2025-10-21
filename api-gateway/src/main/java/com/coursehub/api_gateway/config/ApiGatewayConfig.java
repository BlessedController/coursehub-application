package com.coursehub.api_gateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("identity-service", r -> r
                        .path("/v1/users/**", "/v1/auth/**")
                        .uri("lb://identity-service"))

                .route("course-service", r -> r
                        .path("/v1/courses/**", "/v1/course-categories/**")
                        .uri("lb://course-service"))

                .route("media-stock-service", r -> r
                        .path("/v1/media/**")
                        .uri("lb://media-stock-service")
                )

                .route("rating-service", r -> r
                        .path("/v1/ratings/**")
                        .uri("lb://rating-service")
                )

                .build();
    }
}