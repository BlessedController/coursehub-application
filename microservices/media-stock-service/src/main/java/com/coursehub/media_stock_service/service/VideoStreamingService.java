package com.coursehub.media_stock_service.service;

import com.coursehub.commons.security.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VideoStreamingService {

    void streamVideo(UserPrincipal principal,
                     String courseId,
                     String creatorId,
                     String videoPath,
                     HttpServletRequest request,
                     HttpServletResponse response);

}