package com.coursehub.course_service.client;

import com.coursehub.course_service.config.FeignConfig;
import com.coursehub.course_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.CacheRequest;

@FeignClient(
        name = "identity-service",
        url = "${course-service.client.identity-service.host}",
        path = "${course-service.client.identity-service.internal-controller-url}",
        configuration = FeignConfig.class

)
public interface IdentityServiceClient {

    @GetMapping("/id")
    ResponseEntity<UserResponse> getUserById(String userId);
}
