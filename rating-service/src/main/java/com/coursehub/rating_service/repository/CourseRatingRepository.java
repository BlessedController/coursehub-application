package com.coursehub.rating_service.repository;

import com.coursehub.rating_service.model.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, String> {
    boolean existsCourseRatingByCourseIdAndUserId(String courseId, String userId);
}
