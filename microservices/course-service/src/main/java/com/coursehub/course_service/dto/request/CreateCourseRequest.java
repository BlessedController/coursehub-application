package com.coursehub.course_service.dto.request;

import com.coursehub.commons.feign.enums.Currency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record CreateCourseRequest(
        @Pattern(regexp = "^[^\\\\/:*?\"<>|]+$", message = "Title cannot contain special characters: \\ / : * ? \" < > |")
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 150, message = "Title cannot exceed 150 characters")
        String title,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        BigDecimal price,

        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull(message = "Categories cannot be null")
        @Size(min = 1, max = 3, message = "You must select between 1 and 3 main categories, each with a valid subcategory")
        Set<String> categories
) {
}
