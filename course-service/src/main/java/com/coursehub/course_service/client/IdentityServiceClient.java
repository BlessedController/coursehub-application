package com.coursehub.course_service.client;

import com.coursehub.course_service.config.FeignConfig;
import com.coursehub.course_service.dto.response.UserSelfResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "identity-service",
        url = "localhost:8081",
        path = "/v1/user",
        configuration = FeignConfig.class

)
public interface IdentityServiceClient {

    @GetMapping("/self")
    ResponseEntity<UserSelfResponse> getSelf();

}
