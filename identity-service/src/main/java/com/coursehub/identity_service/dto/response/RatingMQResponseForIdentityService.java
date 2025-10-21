package com.coursehub.identity_service.dto.response;

public record RatingMQResponseForIdentityService(
        String instructorId,
        Double rating
) {
}
