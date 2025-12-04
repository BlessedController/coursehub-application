package com.coursehub.course_service.dto.response;

import com.coursehub.commons.feign.UserResponse;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Builder
public record CreatorCourseResponse(
        String id,
        String title,
        String description,
        BigDecimal price,
        Set<String> categoryIds
        //TODO: immediately add video ids
) implements Serializable {
}
