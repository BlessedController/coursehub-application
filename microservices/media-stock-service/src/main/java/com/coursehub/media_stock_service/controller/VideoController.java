package com.coursehub.media_stock_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.service.VideoService;
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
public class VideoController {

    private final VideoService videoService;

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideoFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("courseId") String courseId,
                                                @RequestParam("displayName") String displayName,
                                                @AuthenticationPrincipal UserPrincipal principal
    ) {

        videoService.uploadVideoFile(file, courseId, displayName, principal);

        return status(CREATED).build();
    }


    @GetMapping("/stream/{creatorId}/{courseId}/{videoPath}/**")
    public ResponseEntity<Void> stream(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String creatorId,
            @PathVariable String courseId,
            @PathVariable String videoPath,
            HttpServletRequest request,
            HttpServletResponse response) {

        System.out.println("================================");
        System.out.println("REQUEST URI: " + request.getRequestURI());
        System.out.println("================================");

        videoService.streamVideo(principal, creatorId, courseId, videoPath, request, response);
        return status(OK).build();
    }

}