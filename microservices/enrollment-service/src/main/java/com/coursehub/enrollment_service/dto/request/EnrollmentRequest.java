package com.coursehub.enrollment_service.dto.request;

import com.coursehub.commons.feign.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotBlank(message = "Course id cannot be null")
        String courseId,

        @NotNull(message = "Payment method must be define")
        PaymentMethod paymentMethod
) {
}
