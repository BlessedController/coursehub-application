package com.coursehub.identity_service.dto.response.user;

import com.coursehub.identity_service.model.enums.Gender;

public record UserResponseForOthers(
        String id,
        String username,
        String email,
        String fullName,
        Gender gender
) {
}
