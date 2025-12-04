package com.coursehub.enrollment_service.controller;

import com.coursehub.enrollment_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("${enrollment-service.internal-controller-base-url}")
@RequiredArgsConstructor
public class InternalController {
    private final InternalService internalService;

    @GetMapping("/has-enrolled/{courseId}/{userId}")
    ResponseEntity<Boolean> hasEnrolledByUser(@PathVariable String courseId,
                                              @PathVariable String userId) {

        Boolean body = internalService.hasEnrolledByUser(courseId, userId);

        return ok(body);
    }

}
