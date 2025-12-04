package com.coursehub.course_service.mapper;

import com.coursehub.course_service.dto.request.UpdateCategoryRequest;
import com.coursehub.course_service.dto.response.CategoryResponse;
import com.coursehub.course_service.model.Category;

public class CategoryMapper {

    public static CategoryResponse toCategoryResponse(Category category) {
        if (category == null) return null;

        Category parent = category.getParentCategory();

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategoryId(parent != null ? parent.getId() : null)
                .build();
    }

    public static void updateCategory(Category category, Category parent, UpdateCategoryRequest request) {

        if (request.name() != null && !request.name().trim().isEmpty()) {
            category.setName(request.name());
        }

        if (parent != null) {
            category.setParentCategory(parent);
        }
    }

}
