package com.coursehub.rating_service.client;

import com.coursehub.rating_service.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static java.lang.Boolean.FALSE;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.ResponseEntity.status;

@FeignClient(
        name = "course-service",
        url = "localhost:8082",
        path = "/v1/course",
        configuration = FeignConfig.class
)
public interface CourseServiceClient {

    // todo: sadəcə publishedlər olmasın inactive(pending olanlara da video əlavə edilə bilsin)
    @GetMapping("/is-exist/{courseId}")
    @CircuitBreaker(name = "isPublishedCourseExistCircuitBreaker", fallbackMethod = "isPublishedCourseExistFallBack")
    ResponseEntity<Boolean> isPublishedCourseExist(@PathVariable String courseId);

}
