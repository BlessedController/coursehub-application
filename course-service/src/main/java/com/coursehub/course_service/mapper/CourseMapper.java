package com.coursehub.course_service.mapper;

import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.CourseResponse;
import com.coursehub.course_service.dto.response.UserSelfResponse;
import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CourseMapper {


    /**
     * Converts a Course entity into a CourseResponse DTO.
     *
     * <p>This method maps all relevant fields of the Course entity to the response object
     * that will be sent to the client. The instructor information is taken from the provided
     * UserSelfResponse.</p>
     *
     * @param course the Course entity to convert; must not be null
     * @param userSelfResponse the user information of the instructor; must not be null
     * @return a CourseResponse containing course details and mapped categories
     */
    public static CourseResponse toCourseResponse(Course course, UserSelfResponse userSelfResponse) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                userSelfResponse,
                course.getPrice(),
                course.getCategories()
                        .stream()
                        .map(CategoryMapper::toResponse)
                        .collect(Collectors.toSet())
        );
    }



    /**
     * Converts a CreateCourseRequest DTO and associated categories into a Course entity.
     *
     * <p>This method is used when creating a new Course. It assigns the instructor ID from
     * the authenticated user (UserPrincipal), sets the provided title, description, price,
     * and associated categories.</p>
     *
     * @param principal the authenticated user creating the course; must not be null
     * @param request the CreateCourseRequest containing the course details; must not be null
     * @param categories the set of Category entities to associate with the course; must not be null
     * @return a new Course entity ready to be persisted
     */
    public static Course toCourseEntity(UserPrincipal principal, CreateCourseRequest request, Set<Category> categories) {
        return Course.builder()
                .title(request.title())
                .description(request.description())
                .instructorId(principal.getId())
                .price(request.price())
                .categories(categories)
                .build();
    }


    /**
     * Updates the fields of a given Course entity based on the provided UpdateCourseRequest
     * and associated categories.
     *
     * <p>This method performs partial updates, meaning only non-null and non-empty fields
     * in the request will be applied to the Course entity. Categories are updated only if
     * a non-null and non-empty set is provided.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *     <li>If {@code request.title()} is non-null and not blank, updates {@code course.title}.</li>
     *     <li>If {@code request.description()} is non-null and not blank, updates {@code course.description}.</li>
     *     <li>If {@code request.price()} is non-null, updates {@code course.price}.</li>
     *     <li>If {@code categories} is non-null and not empty, updates {@code course.categories}.</li>
     * </ul>
     *
     * <p>Note: This method does <b>not</b> persist the changes to the database.
     * The calling service is responsible for saving the updated entity.</p>
     *
     * @param course the Course entity to be updated; must not be null
     * @param categories a set of Category entities to associate with the course; may be null or empty
     * @param request the UpdateCourseRequest containing new values for the course; must not be null
     */
    public static void updateCourse(Course course, Set<Category> categories, UpdateCourseRequest request) {
        if (request.title() != null && !request.title().trim().isEmpty()) {
            course.setTitle(request.title().trim());
        }

        if (request.description() != null && !request.description().trim().isEmpty()) {
            course.setDescription(request.description().trim());
        }

        if (request.price() != null) {
            course.setPrice(request.price());
        }

        if (categories != null && !categories.isEmpty()) {
            course.setCategories(categories);
        }
    }




}
