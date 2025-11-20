package com.coursehub.media_stock_service.service.abstracts;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.coursehub.commons.security.UserPrincipal;


@Service
public interface PhotoService {

    void uploadProfilePhoto(MultipartFile file, UserPrincipal principal);

    void deleteProfilePhoto(UserPrincipal principal);
}
