package com.coursehub.enrollment_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.enrollment_service.dto.request.EnrollmentRequest;
import com.coursehub.enrollment_service.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${enrollment-service.enrollment-controller-base-url}")
@RequiredArgsConstructor
public class UserEnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<Void> enroll(@RequestBody EnrollmentRequest request,
                                       @AuthenticationPrincipal UserPrincipal principal) {

        enrollmentService.enroll(request, principal);

        return ResponseEntity.ok().build();
    }


}
