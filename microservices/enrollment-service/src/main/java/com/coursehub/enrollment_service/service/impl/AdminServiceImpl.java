package com.coursehub.enrollment_service.service.impl;

import com.coursehub.enrollment_service.repository.EnrollmentRepository;
import com.coursehub.enrollment_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final EnrollmentRepository enrollmentRepository;
}
