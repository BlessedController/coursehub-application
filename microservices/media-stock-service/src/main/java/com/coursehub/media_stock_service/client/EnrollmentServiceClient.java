package com.coursehub.media_stock_service.client;

import com.coursehub.media_stock_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "enrollment-service",
        url = "${media-stock-service.client.enrollment-service.host}",
        path = "${media-stock-service.client.enrollment-service.internal-controller-url}",
        configuration = FeignConfig.class
)
public interface EnrollmentServiceClient {

    @GetMapping("/has-enrolled/{courseId}/{userId}")
    ResponseEntity<Boolean> hasEnrolledByUser(@PathVariable String courseId,
                                              @PathVariable String userId);




}
