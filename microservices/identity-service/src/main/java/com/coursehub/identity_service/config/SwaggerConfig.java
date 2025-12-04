package com.coursehub.identity_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI authOpenAPI() {

        // 🔐 Authorization Header (Bearer Token)
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Enter your JWT token in the format: **Bearer <token>**");

        // 📘 Genel Bilgi
        Info info = new Info()
                .title("🔐 CourseHub User Service API")
                .description("""
                        Handles:
                        • User registration & email verification
                        • Login / logout
                        • JWT Access + Refresh token management
                        • Password & profile updates
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
