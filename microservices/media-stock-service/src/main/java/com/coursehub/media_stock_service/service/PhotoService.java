package com.coursehub.media_stock_service.service;

import com.coursehub.commons.security.model.UserPrincipal;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {

    void uploadUserProfilePicture(MultipartFile file, UserPrincipal principal);

    void uploadCoursePosterPicture(MultipartFile file, String courseId, UserPrincipal principal);

    void uploadVideoThumbnail(MultipartFile file, String courseId, String videoId, UserPrincipal principal);
}