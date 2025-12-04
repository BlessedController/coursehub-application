package com.coursehub.identity_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ForgottenPasswordResetRequest(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Reset code cannot be blank")
        String resetCode,

        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String password,

        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String passwordTheSecond

) {
}

