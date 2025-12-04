package com.coursehub.identity_service.dto;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
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
