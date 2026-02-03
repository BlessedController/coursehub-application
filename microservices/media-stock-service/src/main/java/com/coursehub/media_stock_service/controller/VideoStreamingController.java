package com.coursehub.media_stock_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.service.VideoProcessingService;
import com.coursehub.media_stock_service.service.VideoStreamingService;
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
public class VideoStreamingController {

    private final VideoStreamingService videoStreamingService;


    @GetMapping("/stream/{creatorId}/{courseId}/{videoPath}/**")
    public ResponseEntity<Void> stream(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String creatorId,
            @PathVariable String courseId,
            @PathVariable String videoPath,
            HttpServletRequest request,
            HttpServletResponse response) {

        videoStreamingService.streamVideo(principal, creatorId, courseId, videoPath, request, response);
        return status(OK).build();
    }

}