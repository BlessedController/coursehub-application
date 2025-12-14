package com.coursehub.course_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditDisplayNameRequest(
        @NotBlank(message = "display name cannot be blank")
        @Size(min = 1, max = 255)
        String displayName
) {
}
