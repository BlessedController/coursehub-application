package com.coursehub.identity_service.dto.response.user;

import com.coursehub.identity_service.model.enums.Gender;

public record PublicUserResponse(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        String profilePictureName,
        Gender gender,
        Double rating,
        int ratingCount
) {
}
