package com.coursehub.course_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        int number,
        int size,
        int totalPages,
        long totalElements
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }
}
