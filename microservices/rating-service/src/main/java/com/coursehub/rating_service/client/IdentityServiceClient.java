package com.coursehub.rating_service.client;

import com.coursehub.rating_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "identity-service",
        url = "${rating-service.client.identity-service.host}",
        path = "${rating-service.client.identity-service.internal-controller-url}",
        configuration = FeignConfig.class
)
public interface IdentityServiceClient {

    @GetMapping("/exists/{contentCreatorId}")
    ResponseEntity<Boolean> isContentCreatorExists(@PathVariable String contentCreatorId);


}
