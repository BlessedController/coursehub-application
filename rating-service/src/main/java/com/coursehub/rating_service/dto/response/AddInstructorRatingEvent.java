package com.coursehub.rating_service.dto.response;

import lombok.Builder;

@Builder
public record AddInstructorRatingEvent(
        String instructorId,
        Double rating
) {
}
