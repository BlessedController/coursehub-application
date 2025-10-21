package com.coursehub.identity_service.dto.request;

import com.coursehub.identity_service.model.enums.Gender;

public record UpdateUserInfoRequest(
        String firstName,
        String middleName,
        String lastName,
        String phoneNumber,
        Gender gender
) {
}
