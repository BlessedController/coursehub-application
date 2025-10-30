package com.coursehub.media_stock_service.client;

import com.coursehub.media_stock_service.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "enrollment-service",
        url = "localhost:8085",
        path = "/v1/enrollments",
        configuration = FeignConfig.class
)
public interface EnrollmentServiceClient {

    //todo: service to service communication. what kind of auth is necesserialy.
    @GetMapping("/access-check/{courseId}/{userId}")
    @CircuitBreaker(name = "hasEnrolledByUserCircuitBreaker")
    ResponseEntity<Boolean> hasEnrolledByUser(@PathVariable String courseId,
                                              @PathVariable String userId);


}
