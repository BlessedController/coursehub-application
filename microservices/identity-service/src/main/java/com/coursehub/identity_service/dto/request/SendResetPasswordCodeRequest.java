package com.coursehub.identity_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SendResetPasswordCodeRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email
) {}
