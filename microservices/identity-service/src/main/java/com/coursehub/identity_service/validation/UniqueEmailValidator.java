package com.coursehub.identity_service.validation;

import com.coursehub.identity_service.service.InternalService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final InternalService internalService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !internalService.existsByEmail(value);
    }
}
