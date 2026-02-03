package com.coursehub.media_stock_service.service;

import com.coursehub.commons.security.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VideoProcessingService {

    void uploadVideoFile(MultipartFile file, String courseId, String displayName, UserPrincipal principal);



}