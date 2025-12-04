package com.coursehub.identity_service.dto.request;

import com.coursehub.identity_service.validation.UniqueEmail;
import com.coursehub.identity_service.validation.UniqueUsername;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @Pattern(
                regexp = "^[^\\\\/:*?\"<>|]+$",
                message = "Username cannot contain special characters: \\ / : * ? \" < > |")
        @NotBlank(message = "Username cannot be blank")
        @Size(max = 30, message = "Username must not exceed 30 characters")
        @UniqueUsername
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        @UniqueEmail
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String password

) {
}
