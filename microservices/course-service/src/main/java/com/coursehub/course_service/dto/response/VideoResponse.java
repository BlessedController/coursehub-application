package com.coursehub.course_service.dto.response;

import lombok.Builder;

@Builder
public record VideoResponse(
        String id,
        String displayName,
        String videoPath,
        String courseId,
        String profilePictureName
) {
}
