package com.coursehub.payment_service.controller;

import com.coursehub.commons.feign.PaymentRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${payment-service.internal-controller-url}")
public class InternalController {

    @PostMapping("/is-payment-successful")
    public ResponseEntity<Boolean> isPaymentSuccessful(@Valid @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(Boolean.TRUE);
    }
}
