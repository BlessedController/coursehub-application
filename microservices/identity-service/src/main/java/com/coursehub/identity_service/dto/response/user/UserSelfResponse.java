package com.coursehub.identity_service.dto.response.user;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.enums.Gender;

import java.time.LocalDateTime;

public record UserSelfResponse(
        String username,
        String email,
        String phoneNumber,
        UserRole userRole,
        String firstName,
        String lastName,
        String profilePictureName,
        Gender gender,
        UserStatus userStatus,
        Boolean isVerified,
        LocalDateTime createdAt

) {
}