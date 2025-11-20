package com.coursehub.course_service.mapper;

import com.coursehub.course_service.dto.request.UpdateCategoryRequest;
import com.coursehub.course_service.dto.response.CategoryResponse;
import com.coursehub.course_service.model.Category;

public class CategoryMapper {

    // todo: recursive is dangerous

    /**
     * Converts a Category entity into a CategoryResponse DTO.
     * This method handles parent categories recursively, so each parent category
     * is also converted into a CategoryResponse, forming a full parent hierarchy.
     *
     * @param category the Category entity to convert
     * @return a CategoryResponse representing the category and its parent hierarchy,
     * or null if the input category is null
     */
    public static CategoryResponse toResponse(Category category) {
        if (category == null) return null;

        Category parent = category.getParentCategory();

        CategoryResponse parentResponse = (parent != null) ? toResponse(parent) : null;

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategory(parentResponse)
                .build();
    }


    //todo: check and improve
    public static CategoryResponse toResponseNotRecursive(Category category) {
        if (category == null) return null;

        CategoryResponse finalResponse = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        while (category.getParentCategory() != null) {

            category = category.getParentCategory();

            finalResponse = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .parentCategory(finalResponse)
                    .build();
        }

        return finalResponse;
    }

    /**
     * Updates the fields of a Category entity based on the provided request and parent category.
     * Only non-null and non-empty name values are applied, and the parent category is updated
     * if a valid parent is provided.
     *
     * @param category the Category entity to be updated
     * @param parent   the parent Category to set, can be null if no change is needed
     * @param request  the UpdateCategoryRequest containing the new name and parentCategoryId
     */
    public static void updateCategory(Category category, Category parent, UpdateCategoryRequest request) {

        if (request.name() != null && !request.name().trim().isEmpty()) {
            category.setName(request.name());
        }

        if (parent != null) {
            category.setParentCategory(parent);
        }
    }


}
