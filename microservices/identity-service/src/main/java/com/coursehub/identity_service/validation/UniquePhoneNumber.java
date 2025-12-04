package com.coursehub.identity_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {UniquePhoneNumberValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhoneNumber {
    String message() default "Phone pageNumber already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
