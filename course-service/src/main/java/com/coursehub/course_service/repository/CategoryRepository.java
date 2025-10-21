package com.coursehub.course_service.repository;

import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.enums.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {


    Page<Category> findAllByStatus(CategoryStatus status, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.status = :status")
    Optional<Category> findByIdAndStatus(@Param("id") String id, @Param("status") CategoryStatus status);

}
