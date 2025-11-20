package com.coursehub.media_stock_service.dto;

import lombok.Builder;

@Builder
public record StreamResponse(
        String videoUrl
) {
}
