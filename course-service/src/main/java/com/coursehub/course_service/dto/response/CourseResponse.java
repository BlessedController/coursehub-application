package com.coursehub.course_service.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record CourseResponse(
        String id,
        String title,
        String description,
        UserSelfResponse instructor,
        BigDecimal price,
        Set<CategoryResponse> categories
) {
}
