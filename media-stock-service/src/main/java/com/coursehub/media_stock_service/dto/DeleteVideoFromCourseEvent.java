package com.coursehub.media_stock_service.dto;

public record DeleteVideoFromCourseEvent(
        String filename,
        String courseId,
        String instructorId
) {
}
