package com.coursehub.rating_service.dto.request;


import jakarta.validation.constraints.*;

public record ContentCreatorRatingRequest(

        @NotBlank
        String contentCreatorId,

        @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
        @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
        double rating

) {
}
