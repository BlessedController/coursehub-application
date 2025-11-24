package com.coursehub.identity_service.dto.response.common;

import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
