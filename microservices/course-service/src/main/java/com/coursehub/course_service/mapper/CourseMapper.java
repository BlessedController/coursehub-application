package com.coursehub.course_service.mapper;

import com.coursehub.commons.feign.UserResponse;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.*;
import com.coursehub.course_service.model.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CourseMapper {

    public static PublicCourseResponse toPublicCourseResponse(Course course, UserResponse userResponse) {
        return new PublicCourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                userResponse,
                course.getPrice(),
                course.getCategories().stream().map(Category::getId).collect(Collectors.toSet()),
                course.getVideos().stream().map(Video::getId).collect(Collectors.toSet()),
                course.getProfilePicture()
        );
    }

    public static AdminCourseResponse toAdminResponse(Course course) {

        List<String> categoryIds = course.getCategories().stream().map(Category::getId).toList();
        List<String> videoIds = course.getVideos().stream().map(Video::getId).toList();


        return AdminCourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructorId())
                .price(course.getPrice())
                .status(course.getStatus())
                .rating(course.getRating())
                .ratingCount(course.getRatingCount())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .categories(categoryIds)
                .videos(videoIds)
                .profilePictureName(course.getProfilePicture())
                .build();
    }

    public static Course toCourseEntity(UserPrincipal principal, CreateCourseRequest request, Set<Category> categories) {
        return Course.builder()
                .title(request.title())
                .description(request.description())
                .instructorId(principal.getId())
                .price(request.price())
                .currency(request.currency())
                .categories(categories)
                .build();
    }

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


    public static CreatorCourseResponse toCreatorCourseResponse(Course course) {
        return new CreatorCourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getCategories().stream().map(Category::getId).collect(Collectors.toSet()),
                course.getVideos().stream().map(Video::getId).collect(Collectors.toSet()),
                course.getProfilePicture()
        );
    }
}
