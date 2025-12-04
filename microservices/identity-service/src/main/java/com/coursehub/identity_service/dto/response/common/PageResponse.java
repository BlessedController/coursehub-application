package com.coursehub.identity_service.dto.response.common;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int size,
        int totalPages,
        long totalElements
) {
}

