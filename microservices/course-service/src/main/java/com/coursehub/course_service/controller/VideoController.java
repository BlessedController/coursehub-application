package com.coursehub.course_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.course_service.dto.request.EditDisplayNameRequest;
import com.coursehub.course_service.dto.response.VideoResponse;
import com.coursehub.course_service.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${course-service.video-base-url}")
@RequiredArgsConstructor

public class VideoController {
    private final VideoService videoService;

    @GetMapping("{videoId}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable String videoId) {
        VideoResponse videoResponse = videoService.getVideoById(videoId);
        return new ResponseEntity<>(videoResponse, HttpStatus.OK);
    }

    @PutMapping("{videoId}")
    public ResponseEntity<VideoResponse> editDisplayName(@AuthenticationPrincipal UserPrincipal principal,
                                                         @PathVariable String videoId,
                                                         @RequestBody EditDisplayNameRequest request) {
        VideoResponse body = videoService.editDisplayName(principal,videoId, request);
        return new ResponseEntity<>(body, HttpStatus.OK);

    }

}
