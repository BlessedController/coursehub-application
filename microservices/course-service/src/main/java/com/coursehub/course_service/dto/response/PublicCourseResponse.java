package com.coursehub.course_service.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
@Builder
public record PublicCourseResponse(
        String id,
        String title,
        String description,
        UserResponse instructor,
        BigDecimal price,
        Set<String> categoryIds
) implements Serializable {
}
