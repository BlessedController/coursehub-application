package com.coursehub.identity_service.dto.request;

import com.coursehub.identity_service.model.enums.Gender;

public record VerifyRequest(
        String phoneNumber,
        String firstName,
        String middleName,
        String lastName,
        Gender gender
) {

}
