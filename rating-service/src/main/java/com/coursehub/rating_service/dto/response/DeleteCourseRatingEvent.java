package com.coursehub.rating_service.dto.response;

import lombok.Builder;

@Builder
public record DeleteCourseRatingEvent(
        String courseId,
        Double rating
) {
}
