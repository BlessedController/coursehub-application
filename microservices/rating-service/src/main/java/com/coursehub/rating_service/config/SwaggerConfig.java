package com.coursehub.rating_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ratingOpenAPI() {

        // Bearer Auth (JWT)
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT token format: **Bearer <token>**");

        Info info = new Info()
                .title("⭐ CourseHub – Rating Service API")
                .description("""
                        Handles:
                        • Rating courses
                        • Rating content creators
                        • Preventing duplicate ratings
                        • Preventing self-rating
                        • Aggregating rating statistics
                        • Publishing events via Kafka
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Məhəbbət Gözəlov")
                        .email("mgzlovcontact@gmail.com")
                        .url("https://github.com/BlessedController"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth));
    }
}
