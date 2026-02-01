package com.coursehub.media_stock_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.service.VideoProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${media-stock-service.video-base-url}")
@RequiredArgsConstructor
public class VideoProcessingController {

    private final VideoProcessingService videoProcessingService;

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideoFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("courseId") String courseId,
                                                @RequestParam("displayName") String displayName,
                                                @AuthenticationPrincipal UserPrincipal principal
    ) {

        videoProcessingService.uploadVideoFile(file, courseId, displayName, principal);

        return status(CREATED).build();
    }


}