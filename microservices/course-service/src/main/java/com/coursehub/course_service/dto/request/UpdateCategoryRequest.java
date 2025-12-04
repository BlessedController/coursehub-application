package com.coursehub.course_service.dto.request;

public record UpdateCategoryRequest(
        String name,
        String parentCategoryId
) {
}
