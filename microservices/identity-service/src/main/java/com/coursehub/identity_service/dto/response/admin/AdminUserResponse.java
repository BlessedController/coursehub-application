package com.coursehub.identity_service.dto.response.admin;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.enums.Gender;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record AdminUserResponse(
        String id,
        String username,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        String activationCode,
        Gender gender,
        String aboutMe,
        Double rating,
        UserRole userRole,
        UserStatus userStatus,
        Boolean isVerified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
