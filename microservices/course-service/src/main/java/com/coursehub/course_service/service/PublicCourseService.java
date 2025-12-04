package com.coursehub.course_service.service;

import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.dto.response.PublicCourseResponse;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public interface PublicCourseService {

    PublicCourseResponse getPublishedCourseById(String courseId);

    Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> status);

    PageResponse<PublicCourseResponse> getAllPublishedCourses(int page,
                                                              int size,
                                                              String sortBy,
                                                              String orderBy);


    PageResponse<PublicCourseResponse> getCoursesByCategory(String categoryId, int page, int size, String sortBy, String orderBy);


    PageResponse<PublicCourseResponse> getPopularCourses(int page, int size, String sortBy, String orderBy);


    PageResponse<PublicCourseResponse> getRecentCourses(int page, int size, String sortBy, String orderBy);

    PageResponse<PublicCourseResponse> filterCourses(int page,
                                                     int size,
                                                     String keyword,
                                                     BigDecimal minPrice,
                                                     BigDecimal maxPrice,
                                                     LocalDateTime minTime,
                                                     LocalDateTime maxTime,
                                                     Double minRating,
                                                     Double maxRating,
                                                     String categoryId,
                                                     String sortBy,
                                                     String orderBy

    );



}
