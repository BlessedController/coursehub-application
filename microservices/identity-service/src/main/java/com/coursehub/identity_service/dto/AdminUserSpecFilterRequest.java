package com.coursehub.identity_service.dto;

import com.coursehub.commons.security.UserRole;
import com.coursehub.commons.security.UserStatus;
import com.coursehub.identity_service.model.enums.Gender;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record AdminUserSpecFilterRequest(
        String keyword,
        UserRole role,
        UserStatus status,
        Gender gender,
        Boolean isVerified,
        LocalDateTime minDate,
        LocalDateTime maxDate,
        Double rating
) {
}
