package com.coursehub.media_stock_service.service;


import com.coursehub.commons.security.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface VideoService {

    void uploadVideoFile(MultipartFile file, String courseId, String displayName, UserPrincipal principal);

    void streamVideo(UserPrincipal principal,
                     String courseId,
                     String creatorId,
                     String videoId,
                     HttpServletRequest request,
                     HttpServletResponse response);

    void deleteVideoFile(UserPrincipal principal, String courseId, String videoId);

}