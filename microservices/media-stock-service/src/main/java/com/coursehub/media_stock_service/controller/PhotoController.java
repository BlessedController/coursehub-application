package com.coursehub.media_stock_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${media-stock-service.photo-base-url}")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(value = "/user-profile-picture-upload/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadUserProfilePhoto(@RequestParam(name = "file") MultipartFile file,
                                                       @AuthenticationPrincipal UserPrincipal principal

    ) {

        photoService.uploadUserProfilePicture(file, principal);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/course-poster-picture-upload/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadCoursePosterPicture(@RequestParam(name = "file") MultipartFile file,
                                                          @RequestParam(name = "courseId") String courseId,
                                                          @AuthenticationPrincipal UserPrincipal principal

    ) {

        photoService.uploadCoursePosterPicture(file, courseId, principal);

        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/video-thumbnail-upload/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideoThumbnail(@RequestParam(name = "file") MultipartFile file,
                                                     @RequestParam(name = "courseId") String courseId,
                                                     @RequestParam(name = "videoId") String videoId,
                                                     @AuthenticationPrincipal UserPrincipal principal

    ) {

        photoService.uploadVideoThumbnail(file, courseId, videoId, principal);

        return ResponseEntity.ok().build();
    }


}
