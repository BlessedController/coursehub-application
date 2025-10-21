package com.coursehub.identity_service.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PRIVATE;

@ConfigurationProperties(prefix = "spring.mail")
@Configuration
@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class MailPropertiesConfig {
    String host;
    int port;
    String username;
    String password;
    String from;

}
