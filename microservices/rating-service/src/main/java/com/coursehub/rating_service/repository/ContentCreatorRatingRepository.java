package com.coursehub.rating_service.repository;

import com.coursehub.rating_service.dto.response.RatingStats;
import com.coursehub.rating_service.model.ContentCreatorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentCreatorRatingRepository extends JpaRepository<ContentCreatorRating, String> {

    @Query("""
            SELECT new com.coursehub.rating_service.dto.response.RatingStats(
                COUNT(cr),
                AVG(cr.rating)
            )
            FROM ContentCreatorRating cr
            WHERE cr.contentCreatorId = :contentCreatorId
            """)
    RatingStats findRatingStats(String contentCreatorId);


    boolean existsByUserIdAndContentCreatorId(String userId, String contentCreatorId);

    Optional<ContentCreatorRating> findByContentCreatorIdAndUserId(String contentCreatorId, String userId);
}
