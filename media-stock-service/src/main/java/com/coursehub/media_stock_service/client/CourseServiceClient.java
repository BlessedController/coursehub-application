package com.coursehub.media_stock_service.client;

import com.coursehub.media_stock_service.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static java.lang.Boolean.FALSE;

@FeignClient(
        name = "course-service",
        path = "/v1/course",
        configuration = FeignConfig.class
)
public interface CourseServiceClient {

    Logger log = LoggerFactory.getLogger(CourseServiceClient.class);

    @GetMapping("/owner-check/{courseId}")
    @CircuitBreaker(name = "isUserOwnerOfCourseCircuitBreaker", fallbackMethod = "isUserOwnerOfCourseFallBack")
    ResponseEntity<Boolean> isUserOwnerOfCourse(@PathVariable(name = "courseId") String courseId);

    default ResponseEntity<Boolean> isUserOwnerOfCourseFallBack(String courseId,
                                                                Throwable throwable) {
        log.info("Course service unavailable for courseId: {}. Error: {}",
                courseId, throwable.getMessage());

        return ResponseEntity.ok(FALSE);
    }


}
