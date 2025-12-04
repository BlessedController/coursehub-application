package com.coursehub.course_service.dto.response;

import com.coursehub.course_service.model.enums.CourseStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record AdminCourseResponse(

        String id,

        String title,

        String description,

        String instructorId,

        BigDecimal price,

        CourseStatus status,

        Double rating,

        int ratingCount,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        List<String> categories,

        List<String> videos
) {
}
