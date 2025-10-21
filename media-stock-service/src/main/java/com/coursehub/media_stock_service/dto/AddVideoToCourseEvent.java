package com.coursehub.media_stock_service.dto;

public record AddVideoToCourseEvent(
        String filename,
        String displayName,
        String courseId,
        String instructorId
) {
}
