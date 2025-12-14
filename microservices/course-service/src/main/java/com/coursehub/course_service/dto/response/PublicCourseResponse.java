package com.coursehub.course_service.dto.response;

import com.coursehub.commons.feign.UserResponse;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
@Builder
public record PublicCourseResponse(
        String id,
        String title,
        String description,
        UserResponse creatorResponse,
        BigDecimal price,
        Set<String> categoryIds,
        Set<String> videoIds,
        String profilePicture
) implements Serializable {
}
