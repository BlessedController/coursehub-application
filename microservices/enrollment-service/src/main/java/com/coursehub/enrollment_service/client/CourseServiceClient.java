package com.coursehub.enrollment_service.client;

import com.coursehub.commons.feign.CoursePriceResponse;
import com.coursehub.enrollment_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "course-service",
        url = "${enrollment-service.client.course-service.host}",
        path = "${enrollment-service.client.course-service.internal-controller-url}",
        configuration = FeignConfig.class
)
public interface CourseServiceClient {

    @GetMapping("/get-price/{courseId}")
    ResponseEntity<CoursePriceResponse> getCoursePrice(@PathVariable String courseId);

}
