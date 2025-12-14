package com.coursehub.media_stock_service.client;

import com.coursehub.media_stock_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "course-service",
        url = "${media-stock-service.client.course-service.host}",
        path = "${media-stock-service.client.course-service.internal-controller-url}",
        configuration = FeignConfig.class
)
public interface CourseServiceClient {

    @GetMapping("/course-owner-check/{courseId}/{userId}")
    ResponseEntity<Boolean> isUserOwnerOfCourse(@PathVariable String courseId, @PathVariable String userId);

    @GetMapping("/video-owner-check/{videoId}/{userId}")
    ResponseEntity<Boolean> isUserOwnerOfVideo(@PathVariable String videoId,  @PathVariable String userId);
}
