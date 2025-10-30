package com.coursehub.media_stock_service.client;

import com.coursehub.media_stock_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "course-service",
        url = "localhost:8082",
        path = "/v1/courses",
        configuration = FeignConfig.class
)
public interface CourseServiceClient {

    @GetMapping("/owner-check/{courseId}")
    ResponseEntity<Boolean> isUserOwnerOfCourse(@PathVariable String courseId);

    @GetMapping("/is-video-belong")
    ResponseEntity<Boolean> isVideoBelongCourse(@RequestParam String courseId,
                                                @RequestParam String videoId);

    //todo error decoder must be defined
    @GetMapping("/video-path/{videoId}")
    ResponseEntity<String> getVideoPathFromVideoId(@PathVariable String videoId);

}
