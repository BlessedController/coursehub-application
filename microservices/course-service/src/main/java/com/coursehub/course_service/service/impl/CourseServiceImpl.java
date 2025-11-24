package com.coursehub.course_service.service.impl;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.exceptions.UnauthorizedOperationException;
import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.course_service.client.IdentityServiceClient;
import com.coursehub.course_service.dto.request.*;
import com.coursehub.course_service.dto.response.*;
import com.coursehub.course_service.mapper.CourseMapper;
import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.repository.CourseRepository;
import com.coursehub.course_service.service.CategoryService;
import com.coursehub.course_service.service.CourseService;
import com.coursehub.course_service.specification.CourseSpecification;
import com.coursehub.course_service.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursehub.commons.security.UserRole.ROLE_ADMIN;
import static com.coursehub.course_service.mapper.CourseMapper.toCourseResponse;
import static com.coursehub.course_service.model.enums.CategoryStatus.ACTIVE;
import static com.coursehub.course_service.model.enums.CourseStatus.*;
import static java.time.temporal.ChronoUnit.MINUTES;


@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryService categoryService;
    private final IdentityServiceClient identityServiceClient;
    private final Double minPopularRating;
    private final long recentDays;
    private final RedisUtil redisUtil;


    public CourseServiceImpl(CourseRepository courseRepository,
                             CategoryService categoryService,
                             IdentityServiceClient identityServiceClient,
                             @Value("${course.courses.popular.min-rating:4.5}") Double minPopularRating,
                             @Value("${course.courses.recent.days:10}") long recentDays,
                             RedisUtil redisUtil) {
        this.courseRepository = courseRepository;
        this.categoryService = categoryService;
        this.identityServiceClient = identityServiceClient;
        this.minPopularRating = minPopularRating;
        this.recentDays = recentDays;
        this.redisUtil = redisUtil;
    }


    @Override
    public PublicCourseResponse createCourse(UserPrincipal principal, CreateCourseRequest request) {
        Set<Category> categories = request.categories().stream()
                .map(categoryId -> categoryService.findByIdAndStatus(categoryId, ACTIVE))
                .collect(Collectors.toSet());

        Course course = CourseMapper.toCourseEntity(principal, request, categories);

        Course savedCourse = courseRepository.save(course);

        PublicCourseResponse publicCourseResponse = CourseMapper.toCourseResponse(savedCourse, this.getUserById(savedCourse.getInstructorId()));

        redisUtil.saveToCache(savedCourse.getId(), publicCourseResponse, 15L, MINUTES);

        return publicCourseResponse;
    }

    @Override
    public PublicCourseResponse updateCourse(String courseId, UserPrincipal principal, UpdateCourseRequest request) {

        Course course = this.findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED, PENDING));

        validateUserIsCourseOwnerOrAdmin(course.getInstructorId(), principal);


        Set<Category> categories = null;

        if (request.categoryIds() != null) {
            categories = request.categoryIds().stream()
                    .map(categoryId -> categoryService.findByIdAndStatus(categoryId, ACTIVE))
                    .collect(Collectors.toSet());
        }

        CourseMapper.updateCourse(course, categories, request);

        Course updatedCourse = courseRepository.save(course);

        PublicCourseResponse publicCourseResponse = toCourseResponse(updatedCourse, this.getUserById(updatedCourse.getInstructorId()));

        redisUtil.saveToCache(updatedCourse.getId(), publicCourseResponse, 15L, MINUTES);

        return publicCourseResponse;
    }


    @Override
    public void publishCourse(String id, UserPrincipal principal) {
        Course course = findCourseByIdAndStatusIn(id, Set.of(PENDING));

        validateUserIsCourseOwnerOrAdmin(course.getInstructorId(), principal);

        course.setStatus(PUBLISHED);
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(String id, UserPrincipal principal) {
        Course course = findCourseByIdAndStatusIn(id, Set.of(PUBLISHED));

        validateUserIsCourseOwnerOrAdmin(course.getInstructorId(), principal);

        course.setStatus(DELETED);
        courseRepository.save(course);
    }

    @Override
    public Page<PublicCourseResponse> getAllPublishedCourses(Pageable pageable) {

        Page<Course> allPublishedCourses = courseRepository.findAllByStatus(PUBLISHED, pageable);


        return allPublishedCourses
                .map(course -> toCourseResponse(course, this.getUserById(course.getInstructorId())));
    }


    @Override
    public Page<PublicCourseResponse> getMyCourses(UserPrincipal principal, Pageable pageable) {
        return courseRepository
                .findAllByInstructorIdEquals(principal.getId(), pageable)
                .map(course -> CourseMapper.toCourseResponse(course, this.getUserById(course.getInstructorId())));
    }

    @Override
    public PublicCourseResponse getCourseById(String courseId) {

        PublicCourseResponse cachedPublicCourseResponse = redisUtil.getDataFromCache(courseId);

        if (cachedPublicCourseResponse != null) {
            return cachedPublicCourseResponse;
        }

        Course course = findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED));

        PublicCourseResponse publicCourseResponse = CourseMapper.toCourseResponse(course, this.getUserById(course.getInstructorId()));

        redisUtil.saveToCache(course.getId(), publicCourseResponse, 15L, MINUTES);

        return publicCourseResponse;
    }


    @Override
    public Page<PublicCourseResponse> getCoursesByCategory(String categoryId, Pageable pageable) {

        Category category = categoryService.findByIdAndStatus(categoryId, ACTIVE);

        return courseRepository
                .findAllByCategoriesAndStatus(Set.of(category), PUBLISHED, pageable)
                .map(course -> CourseMapper.toCourseResponse(course, this.getUserById(course.getInstructorId())));

    }

    @Override
    public Page<PublicCourseResponse> searchCourses(String keyword, Pageable pageable) {

        if (!StringUtils.hasText(keyword)) {
            return courseRepository
                    .findAllByStatus(PUBLISHED, pageable)
                    .map(course -> CourseMapper.toCourseResponse(course, this.getUserById(course.getInstructorId())));
        }

        return courseRepository
                .searchByTitleOrDescription(keyword, pageable)
                .map(course -> CourseMapper.toCourseResponse(course, this.getUserById(course.getInstructorId())));
    }

    @Override
    public Page<PublicCourseResponse> getPopularCourses(Pageable pageable) {
        return courseRepository
                .findAllByRatingGreaterThan(minPopularRating, pageable)
                .map(course -> CourseMapper.toCourseResponse(course, this.getUserById(course.getInstructorId())));
    }

    @Override
    public Page<PublicCourseResponse> getRecentCourses(Pageable pageable) {

        var createdAfter = LocalDateTime.now().minusDays(recentDays);

        return courseRepository
                .findAllByCreatedAtAfter(createdAfter, pageable)
                .map(course -> CourseMapper.toCourseResponse(course, this.getUserById(course.getInstructorId())));
    }


    //todo: impossible to accest this for public users. write another spec for public users
    @Override
    public PageResponse<PublicCourseResponse> filterCourses(int page, int size, CourseFilterRequest filter) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Course> courseSpecification = CourseSpecification.filter(filter);

        Page<Course> result = courseRepository.findAll(courseSpecification, pageable);

        return PageResponse.of(result.map(a -> CourseMapper.toCourseResponse(a, getUserById(a.getInstructorId()))));
    }

    @Override
    public Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> statuses) {
        return courseRepository.findByIdAndStatusIn(id, statuses)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private void validateUserIsCourseOwnerOrAdmin(String instructorId, UserPrincipal principal) {

        if (principal.getAuthorities().contains(ROLE_ADMIN)) return;

        if (Objects.equals(instructorId, principal.getId())) return;

        throw new UnauthorizedOperationException("You are not the owner of this course");
    }

    private UserResponse getUserById(String userId) {
        return identityServiceClient.getUserById(userId).getBody();
    }


}
