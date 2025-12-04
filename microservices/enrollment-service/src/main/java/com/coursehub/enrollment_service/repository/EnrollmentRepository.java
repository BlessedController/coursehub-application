package com.coursehub.enrollment_service.repository;

import com.coursehub.enrollment_service.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EnrollmentRepository extends JpaRepository<Enrollment, String>, JpaSpecificationExecutor<Enrollment> {

    boolean existsByCourseIdAndUserId(String s, String id);

}
