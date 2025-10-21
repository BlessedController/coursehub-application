package com.coursehub.course_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record CategoryResponse(
        String id,
        String name,
        CategoryResponse parentCategory
) {
}
