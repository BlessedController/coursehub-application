package com.coursehub.identity_service.validation;

import com.coursehub.identity_service.service.abstracts.AuthService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final AuthService AuthService;

    public UniqueUsernameValidator(AuthService AuthService) {
        this.AuthService = AuthService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !AuthService.existsByUsername(value);
    }

}
