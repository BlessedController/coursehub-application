package com.coursehub.course_service.service.abstracts;

import com.coursehub.course_service.dto.request.CreateCategoryRequest;
import com.coursehub.course_service.dto.request.UpdateCategoryRequest;
import com.coursehub.course_service.dto.response.CategoryResponse;
import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.enums.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {

    Category findByIdAndStatus(String id, CategoryStatus status);

    CategoryResponse getActiveCategoryById(String id);

    Page<CategoryResponse> getAllActiveCategories(Pageable pageable);

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(String id, UpdateCategoryRequest request);

    void activateCategory(String id);

    void deActivateCategory(String id);

    void deleteCategory(String id);


}
