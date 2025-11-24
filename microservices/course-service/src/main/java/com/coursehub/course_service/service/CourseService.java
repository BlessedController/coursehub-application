package com.coursehub.course_service.service;

import com.coursehub.course_service.dto.request.*;
import com.coursehub.course_service.dto.response.*;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.commons.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CourseService {

    PublicCourseResponse createCourse(UserPrincipal principal, CreateCourseRequest request);

    PublicCourseResponse updateCourse(String id, UserPrincipal principal, UpdateCourseRequest request);

    void publishCourse(String id, UserPrincipal principal);

    void deleteCourse(String id, UserPrincipal principal);

    Page<PublicCourseResponse> getAllPublishedCourses(Pageable pageable);

    Page<PublicCourseResponse> getMyCourses(UserPrincipal principal, Pageable pageable);

    PublicCourseResponse getCourseById(String courseId);

    Page<PublicCourseResponse> getCoursesByCategory(String categoryId, Pageable pageable);

    Page<PublicCourseResponse> searchCourses(String keyword, Pageable pageable);

    Page<PublicCourseResponse> getPopularCourses(Pageable pageable);

    Page<PublicCourseResponse> getRecentCourses(Pageable pageable);

    PageResponse<PublicCourseResponse> filterCourses(int page, int size, CourseFilterRequest filter);

    Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> status);

}
