package com.coursehub.enrollment_service.client;

import com.coursehub.commons.feign.PaymentRequest;
import com.coursehub.enrollment_service.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-service",
        url = "${enrollment-service.client.payment-service.host}",
        path = "${enrollment-service.client.payment-service.internal-controller-url}",
        configuration = FeignConfig.class
)
public interface PaymentServiceClient {

    Logger log = LoggerFactory.getLogger(PaymentServiceClient.class);


    @PostMapping("/is-payment-successful")
    @CircuitBreaker(name = "isPaymentSuccessfulCircuitBreaker", fallbackMethod = "isPaymentSuccessfulFallBack")
    ResponseEntity<Boolean> isPaymentSuccessful(@Valid @RequestBody PaymentRequest request);

    default ResponseEntity<Boolean> isPaymentSuccessfulFallBack(Throwable throwable) {

        log.error("Fallback triggered in isPaymentSuccessfulFallBack. cause={}, message={}",
                throwable.getClass().getSimpleName(),
                throwable.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

}
