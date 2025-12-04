package com.coursehub.course_service.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int size,
        int totalPages,
        long totalElements
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(Collections.emptyList())
                .pageNumber(0)
                .size(0)
                .totalPages(0)
                .totalElements(0)
                .build();
    }

}
