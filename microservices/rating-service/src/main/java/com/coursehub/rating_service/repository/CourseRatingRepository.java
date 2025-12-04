package com.coursehub.rating_service.repository;

import com.coursehub.rating_service.dto.response.RatingStats;
import com.coursehub.rating_service.model.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, String> {
    boolean existsByUserIdAndCourseId(String userId, String courseId);

    Optional<CourseRating> findByCourseIdAndUserId(String courseId, String userId);


    @Query("""
       SELECT new com.coursehub.rating_service.dto.response.RatingStats(
           COUNT(cr),
           AVG(cr.rating)
       )
       FROM CourseRating cr
       WHERE cr.courseId = :courseId
       """)
    RatingStats findRatingStats(String courseId);


}
