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
        name = "identity-service",
        url = "localhost:8081",
        path = "/v1/users",
        configuration = FeignConfig.class
)
public interface IdentityServiceClient {

    @GetMapping("/is-exist/{instructorId}")
    @CircuitBreaker(name = "isInstructorExistCircuitBreaker", fallbackMethod = "isInstructorExistFallBack")
    ResponseEntity<Boolean> isInstructorExist(@PathVariable String instructorId);

}
