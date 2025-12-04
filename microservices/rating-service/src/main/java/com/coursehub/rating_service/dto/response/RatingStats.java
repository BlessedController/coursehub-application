package com.coursehub.rating_service.dto.response;

import lombok.Builder;

@Builder
public record RatingStats(
        long ratingCount,
        Double averageRating
) {
}
