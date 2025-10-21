package com.coursehub.course_service.dto.response;

import lombok.Builder;

@Builder
public record RatingMQResponseForCourseService(
        String courseId,
        Double rating
) {
}
