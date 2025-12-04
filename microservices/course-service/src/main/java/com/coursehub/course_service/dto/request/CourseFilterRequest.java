package com.coursehub.course_service.dto.request;

import com.coursehub.course_service.model.enums.CourseStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Builder
public record CourseFilterRequest(
        CourseStatus status,
        String keyword,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        LocalDateTime minTime,
        LocalDateTime maxTime,
        Double minRating,
        Double maxRating,
        String category
) {}
