package com.coursehub.course_service.controller;

import com.coursehub.course_service.dto.request.CreateCategoryRequest;
import com.coursehub.course_service.dto.request.UpdateCategoryRequest;
import com.coursehub.course_service.dto.response.CategoryResponse;
import com.coursehub.course_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("${course-service.categories-base-url}")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllActiveCategories(
            @PageableDefault(sort = "id", direction = ASC) Pageable pageable) {
        Page<CategoryResponse> responseList = categoryService.getAllActiveCategories(pageable);
        return ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getActiveCategoryById(@PathVariable String id) {
        CategoryResponse response = categoryService.getActiveCategoryById(id);
        return ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return status(CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable String id,
                                                           @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCategory(@PathVariable String id) {
        categoryService.activateCategory(id);
        return noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deActivateCategory(@PathVariable String id) {
        categoryService.deActivateCategory(id);
        return noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return noContent().build();
    }


}



