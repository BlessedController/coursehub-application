package com.coursehub.identity_service.dto.response.admin;

import com.coursehub.commons.security.UserRole;
import com.coursehub.commons.security.UserStatus;
import com.coursehub.identity_service.model.enums.*;

public record AdminUserResponse(
        String id,
        String username,
        String email,
        String phoneNumber,
        UserRole userRole,
        String firstName,
        String middleName,
        String lastName,
        Gender gender,
        UserStatus userStatus,
        Boolean isVerified
) {
}
