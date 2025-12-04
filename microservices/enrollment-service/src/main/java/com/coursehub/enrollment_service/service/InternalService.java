package com.coursehub.enrollment_service.service;

public interface InternalService {
    Boolean hasEnrolledByUser(String courseId, String userId);
}
