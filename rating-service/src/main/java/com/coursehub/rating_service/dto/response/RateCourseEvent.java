package com.coursehub.rating_service.dto.response;

import lombok.Builder;

@Builder
public record RateCourseEvent(
        String courseId,
        Double rating
) {
}
