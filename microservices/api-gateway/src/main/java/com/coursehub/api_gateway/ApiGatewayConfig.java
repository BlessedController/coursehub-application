package com.coursehub.api_gateway;

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
                        .path("/v1/api/users/**")
                        .uri("http://localhost:8081"))

                .route("course-service", r -> r
                        .path("/v1/api/courses/**")
                        .uri("http://localhost:8082"))

                .route("media-stock-service", r -> r
                        .path("/v1/api/media/**")
                        .uri("http://localhost:8083"))

                .route("rating-service", r -> r
                        .path("/v1/api/ratings/**")
                        .uri("http://localhost:8084"))

                .build();
    }
}
