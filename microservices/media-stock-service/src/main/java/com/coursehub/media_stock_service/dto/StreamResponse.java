package com.coursehub.media_stock_service.dto;

import lombok.Builder;
import org.springframework.http.MediaType;

@Builder
public record StreamResponse(
        MediaType mediaType,
        byte[] videoBytes
) {
}
