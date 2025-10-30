package com.coursehub.media_stock_service.service.abstracts;


import com.coursehub.media_stock_service.dto.StreamResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.coursehub.commons.security.UserPrincipal;

@Service
public interface VideoService {

    void uploadVideoFile(MultipartFile file, String courseId, String displayName, UserPrincipal principal);

    StreamResponse streamVideo(String courseId, String videoId, UserPrincipal principal);

    void deleteVideoFile(UserPrincipal principal, String courseId, String videoId);

}