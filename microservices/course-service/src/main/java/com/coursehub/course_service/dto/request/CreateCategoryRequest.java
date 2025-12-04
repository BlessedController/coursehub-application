package com.coursehub.course_service.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCategoryRequest(
        @NotNull(message = "name cannot be null")
        String name,

        String parentCategoryId
) {
}
