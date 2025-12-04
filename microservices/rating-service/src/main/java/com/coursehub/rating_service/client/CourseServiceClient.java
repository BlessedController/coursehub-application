package com.coursehub.rating_service.client;

import com.coursehub.rating_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "course-service",
        url = "${rating-service.client.course-service.host}",
        path = "${rating-service.client.course-service.internal-controller-url}",
        configuration = FeignConfig.class
)
public interface CourseServiceClient {

    @GetMapping("/owner-check/{courseId}/{userId}")
    ResponseEntity<Boolean> isUserOwnerOfCourse(@PathVariable String courseId, @PathVariable String userId);

    @GetMapping("/is-exist/{courseId}")
    ResponseEntity<Boolean> isPublishedCourseExist(@PathVariable String courseId);



}
