package com.coursehub.identity_service.dto.response.user;

import com.coursehub.identity_service.model.enums.Gender;
import com.coursehub.identity_service.model.enums.UserRole;
import com.coursehub.identity_service.model.enums.UserStatus;

import java.time.LocalDateTime;

public record UserSelfResponse(
        String username,
        String email,
        String phoneNumber,
        UserRole userRole,
        String firstName,
        String middleName,
        String lastName,
        Gender gender,
        UserStatus userStatus,
        Boolean isVerified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}