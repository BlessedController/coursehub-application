package com.coursehub.course_service.service.concretes;

import com.coursehub.course_service.dto.request.CreateCategoryRequest;
import com.coursehub.course_service.dto.request.UpdateCategoryRequest;
import com.coursehub.course_service.dto.response.CategoryResponse;
import com.coursehub.course_service.exception.NotFoundException;
import com.coursehub.course_service.mapper.CategoryMapper;
import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.enums.CategoryStatus;
import com.coursehub.course_service.repository.CategoryRepository;
import com.coursehub.course_service.service.abstracts.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.coursehub.course_service.mapper.CategoryMapper.toResponse;
import static com.coursehub.course_service.model.enums.CategoryStatus.*;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    CategoryRepository categoryRepository;

    // todo:

    /**
     * Finds a Category entity by its id.
     * <p>
     * ⚠️ This method returns the entity itself and therefore must NOT be used
     * directly in the controller layer. Controllers should use
     * {@link #getActiveCategoryById(String)} or other DTO-returning methods instead.
     *
     * <p>Method flow:</p>
     * <ol>
     *   <li>If the given id is {@code null}, returns {@code null} immediately.</li>
     *   <li>Tries to fetch the category from the database by id.</li>
     *   <li>If no category is found, throws {@link NotFoundException}.</li>
     *   <li>If the category exists but has status {@code DELETED},
     *       throws {@link NotFoundException} as well.</li>
     *   <li>Otherwise, returns the category entity.</li>
     * </ol>
     */

    @Override
    public Category findByIdAndStatus(String id, CategoryStatus status) {
        log.info("METHOD: findByIdAndStatus started with id: {}", id);

        if (id == null) {
            log.warn("METHOD: findByIdAndStatus finished: id is null, returning null");
            return null;
        }

        Category category = categoryRepository.findByIdAndStatus(id, status).orElseThrow(() -> {
            log.error("METHOD: findByIdAndStatus failed: category with id {} not found", id);
            return new NotFoundException(String.format("Category with id %s not found", id));
        });

        log.info("METHOD: findByIdAndStatus finished successfully for id: {}", id);
        return category;
    }


    @Override
    public CategoryResponse getActiveCategoryById(String id) {
        log.info("METHOD: getActiveCategoryById started with id: {}", id);
        Category category = findByIdAndStatus(id, ACTIVE);
        log.info("METHOD: getActiveCategoryById finished successfully for id: {}", id);
        return toResponse(category);
    }

    @Override
    public Page<CategoryResponse> getAllActiveCategories(Pageable pageable) {
        log.info("METHOD: getAllActiveCategories started");

        Page<Category> categories = categoryRepository.findAllByStatus(ACTIVE, pageable);

        Page<CategoryResponse> responseList = categories.map(CategoryMapper::toResponse);

        log.info("METHOD: getAllActiveCategories finished successfully");
        return responseList;
    }


    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("METHOD: createCategory started with name: {}", request.name());

        Category parent = findByIdAndStatus(request.parentCategoryId(), ACTIVE);

        Category category = Category.builder()
                .name(request.name())
                .parentCategory(parent)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("METHOD: createCategory finished successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public CategoryResponse updateCategory(String id, UpdateCategoryRequest request) {
        log.info("METHOD: updateCategory started with id: {}", id);

        Category category = findByIdAndStatus(id, ACTIVE);

        Category parent = findByIdAndStatus(request.parentCategoryId(), ACTIVE);

        CategoryMapper.updateCategory(category, parent, request);

        Category saved = categoryRepository.save(category);
        log.info("METHOD: updateCategory finished successfully for id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public void activateCategory(String id) {
        log.info("METHOD: activateCategory started with id: {}", id);

        Category category = findByIdAndStatus(id, INACTIVE);

        category.setStatus(ACTIVE);

        categoryRepository.save(category);

        log.info("METHOD: activateCategory finished: category {} activated", id);
    }

    @Override
    public void deActivateCategory(String id) {
        log.info("METHOD: deActivateCategory started with id: {}", id);
        Category category = findByIdAndStatus(id, ACTIVE);

        if (ACTIVE.equals(category.getStatus())) {
            category.setStatus(INACTIVE);
            categoryRepository.save(category);
            log.info("METHOD: deActivateCategory finished: category {} deactivated", id);
        } else {
            log.debug("METHOD: deActivateCategory finished: category {} already inactive", id);
        }
    }

    @Override
    public void deleteCategory(String id) {
        log.info("METHOD: deleteCategory started with id: {}", id);
        Category category = findByIdAndStatus(id, ACTIVE);

        category.setStatus(DELETED);
        categoryRepository.save(category);

        log.info("METHOD: deleteCategory finished successfully for id: {}", id);
    }


}
