package com.coursehub.course_service.service;


import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.course_service.dto.request.EditDisplayNameRequest;
import com.coursehub.course_service.dto.response.VideoResponse;

public interface VideoService {
    VideoResponse getVideoById(String id);

    VideoResponse editDisplayName(UserPrincipal principal, String videoId, EditDisplayNameRequest request);
}
