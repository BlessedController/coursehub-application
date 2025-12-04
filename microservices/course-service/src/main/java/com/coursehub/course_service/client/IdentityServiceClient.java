package com.coursehub.course_service.client;

import com.coursehub.commons.feign.UserResponse;
import com.coursehub.course_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        name = "identity-service",
        url = "${course-service.client.identity-service.host}",
        path = "${course-service.client.identity-service.internal-controller-url}",
        configuration = FeignConfig.class

)
public interface IdentityServiceClient {

    @GetMapping("/{userId}")
    ResponseEntity<UserResponse> getUserById(@PathVariable String userId);


    @PostMapping("/batch")
    ResponseEntity<List<UserResponse>> getUsersBatch(@RequestBody List<String> ids);

}
