package com.coursehub.enrollment_service.service.impl;

import com.coursehub.enrollment_service.repository.EnrollmentRepository;
import com.coursehub.enrollment_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalServiceImpl implements InternalService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public Boolean hasEnrolledByUser(String courseId, String userId) {
        return enrollmentRepository.existsByCourseIdAndUserId(courseId, userId);
    }
}
