package com.coursehub.course_service.service.impl;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.exceptions.UnauthorizedOperationException;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.CreatorCourseResponse;
import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.mapper.CourseMapper;
import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.repository.CourseRepository;
import com.coursehub.course_service.service.CategoryService;
import com.coursehub.course_service.service.CreatorCourseService;
import com.coursehub.course_service.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursehub.commons.security.model.UserRole.ROLE_ADMIN;
import static com.coursehub.course_service.model.enums.CategoryStatus.ACTIVE;
import static com.coursehub.course_service.model.enums.CourseStatus.*;
import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatorCourseServiceImpl implements CreatorCourseService {

    private final CourseRepository courseRepository;
    private final CategoryService categoryService;
    private final RedisUtil redisUtil;


    @Override
    public CreatorCourseResponse createCourse(UserPrincipal principal, CreateCourseRequest request) {
        Set<Category> categories = request.categories().stream()
                .map(categoryId -> categoryService.findByIdAndStatus(categoryId, ACTIVE))
                .collect(Collectors.toSet());

        Course course = CourseMapper.toCourseEntity(principal, request, categories);

        Course savedCourse = courseRepository.save(course);

        CreatorCourseResponse CreatorCourseResponse = CourseMapper.toCreatorCourseResponse(savedCourse);

        redisUtil.saveToCache(savedCourse.getId(), CreatorCourseResponse, 15L, MINUTES);

        return CreatorCourseResponse;
    }

    @Override
    public CreatorCourseResponse updateCourse(String courseId, UserPrincipal principal, UpdateCourseRequest request) {

        Course course = this.findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED, PENDING));

        this.validateUserIsCourseOwnerOrAdmin(course.getInstructorId(), principal);

        Set<Category> categories = null;

        if (request.categoryIds() != null) {
            categories = request.categoryIds().stream()
                    .map(categoryId -> categoryService.findByIdAndStatus(categoryId, ACTIVE))
                    .collect(Collectors.toSet());
        }

        CourseMapper.updateCourse(course, categories, request);

        Course updatedCourse = courseRepository.save(course);

        CreatorCourseResponse CreatorCourseResponse = CourseMapper.toCreatorCourseResponse(updatedCourse);

        redisUtil.saveToCache(updatedCourse.getId(), CreatorCourseResponse, 15L, MINUTES);

        return CreatorCourseResponse;
    }


    @Override
    public PageResponse<CreatorCourseResponse> getMyCourses(UserPrincipal principal, int page, int size, String sortBy, String orderBy) {

        Pageable pageable = this.getPageable(page, size, sortBy, orderBy);


        Page<Course> coursePages = courseRepository.getCoursesByInstructorId(principal.getId(), pageable);

        Page<CreatorCourseResponse> responsePages = coursePages.map(CourseMapper::toCreatorCourseResponse);


        return PageResponse.<CreatorCourseResponse>builder()
                .content(responsePages.getContent())
                .pageNumber(responsePages.getNumber()+1)
                .size(responsePages.getSize())
                .totalElements(responsePages.getTotalElements())
                .totalPages(responsePages.getTotalPages())
                .build();
    }

    @Override
    public void publishCourse(String id, UserPrincipal principal) {
        Course course = findCourseByIdAndStatusIn(id, Set.of(PENDING));

        this.validateUserIsCourseOwnerOrAdmin(course.getInstructorId(), principal);

        course.setStatus(PUBLISHED);
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(String id, UserPrincipal principal) {
        Course course = findCourseByIdAndStatusIn(id, Set.of(PUBLISHED));

        this.validateUserIsCourseOwnerOrAdmin(course.getInstructorId(), principal);

        course.setStatus(DELETED);
        courseRepository.save(course);
    }

    @Override
    public Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> statuses) {
        return courseRepository.findByIdAndStatusIn(id, statuses)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private Pageable getPageable(int rawPageNumber, int rawSize, String sortBy, String orderBy) {
        int normalizedPage = rawPageNumber <= 0 ? 1 : rawPageNumber;
        int normalizedSize = rawSize <= 0 ? 10 : rawSize;

        if (!"asc".equalsIgnoreCase(orderBy.trim()) && !"desc".equalsIgnoreCase(orderBy.trim())) {
            orderBy = "desc";
        }

        Sort.Direction direction = Sort.Direction.fromString(orderBy);

        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(normalizedPage-1, normalizedSize, sort);
    }

    private void validateUserIsCourseOwnerOrAdmin(String instructorId, UserPrincipal principal) {

        if (principal.getAuthorities().contains(ROLE_ADMIN)) return;

        if (Objects.equals(instructorId, principal.getId())) return;

        throw new UnauthorizedOperationException("You are not the owner of this course");
    }


}
