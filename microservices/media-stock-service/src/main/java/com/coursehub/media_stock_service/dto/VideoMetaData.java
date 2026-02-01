package com.coursehub.media_stock_service.dto;

public record VideoMetaData(
        String randomVideoName,
        String displayName,
        String contentCreatorId,
        String courseId,
        double videoDuration
) {
}
