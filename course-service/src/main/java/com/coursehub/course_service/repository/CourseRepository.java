package com.coursehub.course_service.repository;

import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;


@Repository
public interface CourseRepository extends JpaRepository<Course, String> {


    @Query("""
            SELECT c
            FROM Course c
            WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Course> searchByTitleOrDescription(
            @Param("keyword") String keyword,
            Pageable pageable
    );


    @Query("""
                SELECT c FROM Course c
                JOIN c.categories cat
                WHERE cat IN :categories
                  AND c.status = :status
            """)
    Page<Course> findAllByCategoriesAndStatus(
            @Param("categories") Set<Category> categories,
            @Param("status") CourseStatus status,
            Pageable pageable
    );

    Optional<Course> findByIdAndStatusIn(String id, Collection<CourseStatus> statuses);

    Page<Course> findAllByStatus(CourseStatus status, Pageable pageable);

    Page<Course> findAllByRatingGreaterThan(Double ratingIsGreaterThan, Pageable pageable);

    Page<Course> findAllByCreatedAtAfter(LocalDateTime createdAtAfter, Pageable pageable);

    Page<Course> findAllByInstructorIdEquals(String instructorId, Pageable pageable);

    Optional<Course> findByIdAndStatusNot(String id, CourseStatus status);

    boolean existsByIdAndStatusIn(String id, Collection<CourseStatus> statuses);

}
