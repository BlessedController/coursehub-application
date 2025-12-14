package com.coursehub.enrollment_service.dto.response;

import lombok.Builder;

import java.util.Set;
@Builder
public record EnrolledCourseResponse(
        Set<String> enrolledCourses
) {
}
