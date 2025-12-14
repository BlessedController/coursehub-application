package com.coursehub.course_service.repository;

import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;


@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByIdAndStatusIn(String id, Collection<CourseStatus> statuses);

    boolean existsByIdAndStatusIn(String id, Collection<CourseStatus> statuses);

    @Query("SELECT c FROM Course c WHERE c.instructorId = :instructorId")
    Page<Course> getCoursesByInstructorId(@Param("instructorId") String instructorId, Pageable pageable);

}
