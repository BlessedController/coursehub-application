package com.coursehub.course_service.service.concretes;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.exceptions.UnauthorizedOperationException;
import com.coursehub.course_service.client.IdentityServiceClient;
import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.*;
import com.coursehub.course_service.mapper.CourseMapper;
import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.repository.CourseRepository;
import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.course_service.service.abstracts.ICategoryService;
import com.coursehub.course_service.service.abstracts.ICourseService;
import com.coursehub.course_service.util.RedisUtil;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coursehub.course_service.mapper.CourseMapper.toCourseEntity;
import static com.coursehub.course_service.mapper.CourseMapper.toCourseResponse;
import static com.coursehub.course_service.model.enums.CategoryStatus.ACTIVE;
import static com.coursehub.course_service.model.enums.CourseStatus.*;
import static com.coursehub.commons.security.UserRole.ROLE_ADMIN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.temporal.ChronoUnit.MINUTES;
import static lombok.AccessLevel.PRIVATE;


@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class CourseService implements ICourseService {

    CourseRepository courseRepository;
    ICategoryService categoryService;
    IdentityServiceClient identityServiceClient;
    Double minPopularRating;
    long recentDays;
    RedisUtil redisUtil;


    public CourseService(CourseRepository courseRepository,
                         ICategoryService categoryService,
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
    public CourseResponse createCourse(UserPrincipal principal, CreateCourseRequest request) {
        UserSelfResponse userSelfResponse = getUserSelfResponse();

        Set<Category> categories = request.categories().stream()
                .map(categoryId -> categoryService.findByIdAndStatus(categoryId, ACTIVE))
                .collect(Collectors.toSet());

        Course course = toCourseEntity(principal, request, categories);

        Course savedCourse = courseRepository.save(course);

        CourseResponse courseResponse = toCourseResponse(savedCourse, userSelfResponse);

        redisUtil.saveToCache(savedCourse.getId(), courseResponse, 15L, MINUTES);

        return courseResponse;
    }

    @Override
    public CourseResponse updateCourse(String courseId, UserPrincipal principal, UpdateCourseRequest request) {

        Course course = findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED, PENDING));

        validateUserIsCourseOwnerOrAdmin(course.getInstructorId(), principal);

        UserSelfResponse userSelfResponse = getUserSelfResponse();

        Set<Category> categories = null;

        if (request.categoryIds() != null) {
            categories = request.categoryIds().stream()
                    .map(categoryId -> categoryService.findByIdAndStatus(categoryId, ACTIVE))
                    .collect(Collectors.toSet());
        }

        CourseMapper.updateCourse(course, categories, request);

        Course updatedCourse = courseRepository.save(course);

        CourseResponse courseResponse = toCourseResponse(updatedCourse, userSelfResponse);

        redisUtil.saveToCache(updatedCourse.getId(), courseResponse, 15L, MINUTES);

        return courseResponse;
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
    public Page<CourseResponse> getAllPublishedCourses(Pageable pageable) {

        Page<Course> allPublishedCourses = courseRepository.findAllByStatus(PUBLISHED, pageable);

        var userSelfResponse = getUserSelfResponse();

        return allPublishedCourses
                .map(course -> toCourseResponse(course, userSelfResponse));
    }


    @Override
    public Page<CourseResponse> getMyCourses(UserPrincipal principal, Pageable pageable) {
        UserSelfResponse userSelfResponse = getUserSelfResponse();

        return courseRepository
                .findAllByInstructorIdEquals(principal.getId(), pageable)
                .map(course -> toCourseResponse(course, userSelfResponse));
    }

    @Override
    public CourseResponse getCourseById(String courseId) {

        CourseResponse cachedCourseResponse = redisUtil.getDataFromCache(courseId);

        if (cachedCourseResponse != null) {
            return cachedCourseResponse;
        }

        UserSelfResponse userSelfResponse = getUserSelfResponse();

        Course course = findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED));

        CourseResponse courseResponse = toCourseResponse(course, userSelfResponse);

        redisUtil.saveToCache(course.getId(), courseResponse, 15L, MINUTES);

        return courseResponse;
    }


    @Override
    public Page<CourseResponse> getCoursesByCategory(String categoryId, Pageable pageable) {

        Category category = categoryService.findByIdAndStatus(categoryId, ACTIVE);

        UserSelfResponse userSelfResponse = getUserSelfResponse();

        return courseRepository
                .findAllByCategoriesAndStatus(Set.of(category), PUBLISHED, pageable)
                .map(course -> toCourseResponse(course, userSelfResponse));

    }

    @Override
    public Page<CourseResponse> searchCourses(String keyword, Pageable pageable) {

        var userSelfResponse = getUserSelfResponse();

        if (!StringUtils.hasText(keyword)) {
            return courseRepository
                    .findAllByStatus(PUBLISHED, pageable)
                    .map(course -> toCourseResponse(course, userSelfResponse));
        }

        return courseRepository
                .searchByTitleOrDescription(keyword, pageable)
                .map(course -> toCourseResponse(course, userSelfResponse));
    }

    @Override
    public Page<CourseResponse> getPopularCourses(Pageable pageable) {

        var userSelfResponse = getUserSelfResponse();

        return courseRepository
                .findAllByRatingGreaterThan(minPopularRating, pageable)
                .map(course -> toCourseResponse(course, userSelfResponse));
    }

    @Override
    public Page<CourseResponse> getRecentCourses(Pageable pageable) {

        var createdAfter = LocalDateTime.now().minusDays(recentDays);

        var userSelfResponse = getUserSelfResponse();

        return courseRepository
                .findAllByCreatedAtAfter(createdAfter, pageable)
                .map(course -> toCourseResponse(course, userSelfResponse));
    }

    @Override
    public Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> statuses) {
        return courseRepository.findByIdAndStatusIn(id, statuses)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }


    @Override
    public boolean isPublishedCourseExist(String courseId) {
        if (!StringUtils.hasText(courseId)) return FALSE;
        return courseRepository.existsByIdAndStatusIn(courseId, Set.of(PUBLISHED));
    }


    @Override
    public Boolean isUserOwnerOfCourse(String courseId, UserPrincipal userPrincipal) {
        Course course = findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED, PENDING));

        if (Objects.equals(course.getInstructorId(), userPrincipal.getId())) return TRUE;
        return FALSE;
    }

    private void validateUserIsCourseOwnerOrAdmin(String instructorId, UserPrincipal principal) {

        if (principal.getAuthorities().contains(ROLE_ADMIN)) return;

        if (Objects.equals(instructorId, principal.getId())) return;

        throw new UnauthorizedOperationException("You are not the owner of this course");
    }

    private UserSelfResponse getUserSelfResponse() {
        return identityServiceClient.getSelf().getBody();
    }


}
