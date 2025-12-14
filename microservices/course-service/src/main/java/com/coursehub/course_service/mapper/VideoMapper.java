package com.coursehub.course_service.mapper;

import com.coursehub.course_service.dto.response.VideoResponse;
import com.coursehub.course_service.model.Video;

public class VideoMapper {
    public static VideoResponse toVideoResponse(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .videoPath(video.getVideoPath())
                .courseId(video.getCourse().getId())
                .displayName(video.getDisplayName())
                .profilePictureName(video.getProfilePictureName())
                .build();
    }
}
