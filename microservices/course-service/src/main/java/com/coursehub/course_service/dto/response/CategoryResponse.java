package com.coursehub.course_service.dto.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record CategoryResponse(
        String id,
        String name,
        String parentCategoryId
) implements Serializable {
}
