package com.coursehub.course_service.service.impl;

import com.coursehub.commons.exceptions.CustomFeignException;
import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.feign.UserResponse;
import com.coursehub.course_service.client.IdentityServiceClient;
import com.coursehub.course_service.dto.request.CourseFilterRequest;
import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.dto.response.PublicCourseResponse;
import com.coursehub.course_service.mapper.CourseMapper;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.repository.CourseRepository;
import com.coursehub.course_service.service.PublicCourseService;
import com.coursehub.course_service.specification.CourseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.coursehub.course_service.model.enums.CourseStatus.PUBLISHED;
import static java.lang.Boolean.TRUE;


@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCourseServiceImpl implements PublicCourseService {

    private final CourseRepository courseRepository;
    private final IdentityServiceClient identityServiceClient;

    @Value("${courses.popular.min-rating}")
    private Double minPopularRating;

    @Value("${courses.recent.days}")
    private long recentDays;


    @Override
    public PublicCourseResponse getPublishedCourseById(String courseId) {

        Course course = this.findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED));

        return CourseMapper.toPublicCourseResponse(course, this.getUserById(course.getInstructorId()));
    }

    @Override
    public Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> statuses) {
        return courseRepository.findByIdAndStatusIn(id, statuses)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    @Override
    public PageResponse<PublicCourseResponse> getAllPublishedCourses(int page,
                                                                     int size,
                                                                     String sortBy,
                                                                     String orderBy) {

        return this.filter(page, size, null, null, null, null, null, null, null, null, null, null, sortBy, orderBy);
    }

    @Override
    public PageResponse<PublicCourseResponse> getCoursesByCategory(String categoryId, int page, int size, String sortBy, String orderBy) {
        return this.filter(page, size, null, null, null, null, null, null, null, categoryId, null, null, sortBy, orderBy);
    }

    @Override
    public PageResponse<PublicCourseResponse> getPopularCourses(int page, int size, String sortBy, String orderBy) {
        return this.filter(page, size, null, null, null, null, null, null, null, null, TRUE, null, sortBy, orderBy);
    }

    @Override
    public PageResponse<PublicCourseResponse> getRecentCourses(int page, int size, String sortBy, String orderBy) {
        return this.filter(page, size, null, null, null, null, null, null, null, null, null, TRUE, sortBy, orderBy);
    }

    @Override
    public PageResponse<PublicCourseResponse> filterCourses(int page, int size, String keyword, BigDecimal minPrice, BigDecimal maxPrice, LocalDateTime minTime, LocalDateTime maxTime, Double minRating, Double maxRating, String categoryId, String sortBy, String orderBy) {
        return this.filter(page, size, keyword, minPrice, maxPrice, minTime, maxTime, minRating, maxRating, categoryId, null, null, sortBy, orderBy);
    }


    private PageResponse<PublicCourseResponse> filter(int page,
                                                      int size,
                                                      String keyword,
                                                      BigDecimal minPrice,
                                                      BigDecimal maxPrice,
                                                      LocalDateTime minTime,
                                                      LocalDateTime maxTime,
                                                      Double minRating,
                                                      Double maxRating,
                                                      String categoryId,
                                                      Boolean isPopular,
                                                      Boolean isRecent,
                                                      String sortBy,
                                                      String orderBy) {


        if (TRUE.equals(isPopular)) {
            minRating = this.minPopularRating;
            maxRating = 5.00;
        }
        if (TRUE.equals(isRecent)) {
            minTime = LocalDateTime.now().minusDays(this.recentDays);
            maxTime = LocalDateTime.now();
        }

        CourseFilterRequest request = CourseFilterRequest.builder()
                .status(PUBLISHED)
                .keyword(keyword)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minTime(minTime)
                .maxTime(maxTime)
                .minRating(minRating)
                .maxRating(maxRating)
                .category(categoryId)
                .build();

        Specification<Course> filter = CourseSpecification.filter(request);

        Pageable pageable = this.getPageable(page, size, sortBy, orderBy);

        Page<Course> coursePages = courseRepository.findAll(filter, pageable);

        List<String> creatorIds = coursePages.stream()
                .map(Course::getInstructorId)
                .distinct()
                .collect(Collectors.toList());


        List<UserResponse> usersBatch;
        try {
            usersBatch = identityServiceClient.getUsersBatch(creatorIds).getBody();
        } catch (feign.RetryableException e) {
            log.error("RetryableException{}", e.getMessage());
            throw new CustomFeignException("RetryableException has ocurred while trying to fetch users batch from Identity Service.");
        }

        if (usersBatch == null) usersBatch = List.of();

        Map<String, UserResponse> userMap =
                usersBatch.stream().collect(Collectors.toMap(UserResponse::id, response -> response));

        Page<PublicCourseResponse> responsePages = coursePages.map(course -> CourseMapper.toPublicCourseResponse(course, userMap.get(course.getInstructorId())));


        return PageResponse.<PublicCourseResponse>builder()
                .content(responsePages.getContent())
                .pageNumber(responsePages.getNumber() + 1)
                .size(responsePages.getSize())
                .totalElements(responsePages.getTotalElements())
                .totalPages(responsePages.getTotalPages())
                .build();

    }


    private UserResponse getUserById(String userId) {
        return identityServiceClient.getUserById(userId).getBody();
    }

    private Pageable getPageable(int rawPageNumber, int rawSize, String sortBy, String orderBy) {
        int normalizedPage = rawPageNumber <= 0 ? 1 : rawPageNumber;
        int normalizedSize = rawSize <= 0 ? 10 : rawSize;

        if (orderBy == null || orderBy.isBlank()) {
            orderBy = "desc";
        }
        orderBy = orderBy.trim().toLowerCase();

        if (!"asc".equals(orderBy) && !"desc".equals(orderBy)) {
            orderBy = "desc";
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt";
        }
        sortBy = sortBy.trim();

        Sort.Direction direction = Sort.Direction.fromString(orderBy);

        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(normalizedPage - 1, normalizedSize, sort);
    }

}
