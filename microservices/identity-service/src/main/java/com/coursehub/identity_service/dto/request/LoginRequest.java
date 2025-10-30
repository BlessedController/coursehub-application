package com.coursehub.identity_service.dto.request;

public record LoginRequest(
        String identifier,
        String password
) {
}
