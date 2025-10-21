package com.coursehub.media_stock_service.controller;

import com.coursehub.media_stock_service.security.UserPrincipal;
import com.coursehub.media_stock_service.service.VideoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/v1/media/video")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VideoController {

    VideoService videoService;

    @PostMapping(value = "/upload/{courseId}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideoFile(@RequestParam("file") MultipartFile file,
                                                @AuthenticationPrincipal UserPrincipal principal,
                                                @PathVariable("courseId") String courseId,
                                                @RequestParam("displayName") String displayName
    ) {

        videoService.uploadVideoFile(file, principal, courseId, displayName);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/delete/{courseId}/{filename}")
    public ResponseEntity<Void> deleteVideo(@AuthenticationPrincipal UserPrincipal principal,
                                            @PathVariable String courseId,
                                            @PathVariable String filename) {

        videoService.deleteVideo(principal, courseId, filename);
        return ResponseEntity.noContent().build();

    }

}