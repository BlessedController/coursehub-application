package com.coursehub.media_stock_service.dto;

public record AddUserProfilePhotoEvent(
        String profilePhotoName,
        String userId
) {
}
