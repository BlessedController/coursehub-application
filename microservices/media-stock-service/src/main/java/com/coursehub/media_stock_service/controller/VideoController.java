package com.coursehub.media_stock_service.controller;

import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.media_stock_service.dto.StreamResponse;
import com.coursehub.media_stock_service.service.abstracts.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${media-stock-service.video-base-url}")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping(value = "/upload/{courseId}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideoFile(@RequestParam("file") MultipartFile file,
                                                @PathVariable("courseId") String courseId,
                                                @RequestParam("displayName") String displayName,
                                                @AuthenticationPrincipal UserPrincipal principal
    ) {

        videoService.uploadVideoFile(file, courseId, displayName, principal);

        return status(CREATED).build();
    }


    @GetMapping
    public ResponseEntity<StreamResponse> streamVideo(
            @RequestParam String courseId,
            @RequestParam String videoId,
            @AuthenticationPrincipal UserPrincipal principal) {

        StreamResponse streamResponse = videoService.streamVideo(courseId, videoId, principal);

        return status(OK).body(streamResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVideoFile(@AuthenticationPrincipal UserPrincipal principal,
                                                @RequestParam String courseId,
                                                @RequestParam String videoId) {

        videoService.deleteVideoFile(principal, courseId, videoId);
        return status(NO_CONTENT).build();

    }
}