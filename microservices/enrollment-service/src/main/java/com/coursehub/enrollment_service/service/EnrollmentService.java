package com.coursehub.enrollment_service.service;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.enrollment_service.dto.request.EnrollmentRequest;

public interface EnrollmentService {
    void enroll(EnrollmentRequest request, UserPrincipal principal);

}
