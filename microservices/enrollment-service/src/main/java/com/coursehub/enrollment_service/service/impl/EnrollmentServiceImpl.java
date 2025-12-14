package com.coursehub.enrollment_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.feign.CoursePriceResponse;
import com.coursehub.commons.feign.PaymentRequest;
import com.coursehub.commons.feign.enums.PaymentMethod;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.enrollment_service.client.CourseServiceClient;
import com.coursehub.enrollment_service.client.PaymentServiceClient;
import com.coursehub.enrollment_service.dto.request.EnrollmentRequest;
import com.coursehub.enrollment_service.dto.response.EnrolledCourseResponse;
import com.coursehub.enrollment_service.model.Enrollment;
import com.coursehub.enrollment_service.repository.EnrollmentRepository;
import com.coursehub.enrollment_service.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseServiceClient courseServiceClient;
    private final PaymentServiceClient paymentServiceClient;


    @Override
    public void enroll(EnrollmentRequest request, UserPrincipal principal) {

        if (request.paymentMethod() == null)
            throw new InvalidRequestException("Payment method must be provided");

        this.validateCourseIsAlreadyEnrolled(request.courseId(), principal.getId());

        var coursePriceResponse = this.getCoursePrice(request.courseId());

        PaymentRequest paymentRequest = this.createPaymentRequest(
                principal.getId(),
                request.courseId(),
                coursePriceResponse,
                request.paymentMethod()
        );

        this.validatePaymentSuccessful(paymentRequest);

        Enrollment enrollment = Enrollment.builder()
                .userId(principal.getId())
                .courseId(request.courseId())
                .build();

        enrollmentRepository.save(enrollment);
    }

    @Override
    public EnrolledCourseResponse getEnrolledCoursesByUserId(UserPrincipal principal) {

        Set<String> enrolledCourseIds = enrollmentRepository.findAllByUserId(principal.getId()).stream()
                .map(Enrollment::getCourseId).collect(Collectors.toSet());

        return EnrolledCourseResponse.builder()
                .enrolledCourses(enrolledCourseIds)
                .build();

    }


    private PaymentRequest createPaymentRequest(String userId, String courseId, CoursePriceResponse
            response, PaymentMethod paymentMethod) {
        return PaymentRequest.builder()
                .paymentId(this.createPaymentId())
                .userId(userId)
                .courseId(courseId)
                .amount(response.amount())
                .currency(response.currency())
                .paymentMethod(paymentMethod)
                .build();
    }

    private void validateCourseIsAlreadyEnrolled(String courseId, String userId) {
        boolean isAlreadyEnrolled = enrollmentRepository.existsByCourseIdAndUserId(courseId, userId);

        if (isAlreadyEnrolled)
            throw new InvalidRequestException("Enrollment already exists");
    }

    private CoursePriceResponse getCoursePrice(String courseId) {
        try {
            CoursePriceResponse coursePriceResponse = courseServiceClient.getCoursePrice(courseId).getBody();

            if (coursePriceResponse == null)
                throw new InvalidRequestException("Course Price is not available");

            return coursePriceResponse;
        } catch (feign.RetryableException e) {
            log.error(e.getMessage());
            throw new CustomFeignException("Course Service is unavailable");
        }
    }

    private void validatePaymentSuccessful(PaymentRequest paymentRequest) {

        ResponseEntity<Boolean> response = paymentServiceClient.isPaymentSuccessful(paymentRequest);

        if (response == null || response.getBody() == null) {
            throw new CustomFeignException("Payment service is unavailable");
        }

        if (!Boolean.TRUE.equals(response.getBody())) {
            throw new InvalidRequestException("Payment is not successful");
        }

    }

    private String createPaymentId() {
        return UUID.randomUUID().toString();
    }
}

