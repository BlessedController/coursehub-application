package com.coursehub.rating_service.repository;

import com.coursehub.rating_service.model.InstructorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRatingRepository extends JpaRepository<InstructorRating, String> {
    boolean existsInstructorRatingByInstructorIdAndUserId(String instructorId, String userId);
}
