package com.coursehub.identity_service.dto.request;

import com.coursehub.identity_service.validation.UniqueEmail;
import com.coursehub.identity_service.validation.UniquePhoneNumber;
import com.coursehub.identity_service.validation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//TODO check must be empty sent but if it is full must be regular
public record UpdateSelfPrincipalsRequest(

        @UniqueUsername
        @Pattern(
                regexp = "^[^\\\\/:*?\"<>|]+$",
                message = "Username cannot contain special characters: \\ / : * ? \" < > |")
        @Size(max = 30, message = "Username must not exceed 30 characters")
        String username,

        @Email
        @UniqueEmail
        String email,

        @UniquePhoneNumber
        String phoneNumber
) {
}
