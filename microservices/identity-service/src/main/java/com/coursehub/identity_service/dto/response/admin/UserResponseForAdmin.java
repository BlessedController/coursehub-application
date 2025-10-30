package com.coursehub.identity_service.dto.response.admin;

import com.coursehub.identity_service.model.enums.*;
import com.coursehub.identity_service.model.enums.UserRole;

public record UserResponseForAdmin(
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
