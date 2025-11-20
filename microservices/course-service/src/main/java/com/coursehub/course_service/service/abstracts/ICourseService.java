package com.coursehub.course_service.service.abstracts;

import com.coursehub.course_service.dto.request.*;
import com.coursehub.course_service.dto.response.CourseResponse;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.commons.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface ICourseService {

    CourseResponse createCourse(UserPrincipal principal, CreateCourseRequest request);

    CourseResponse updateCourse(String id, UserPrincipal principal, UpdateCourseRequest request);

    void publishCourse(String id, UserPrincipal principal);

    void deleteCourse(String id, UserPrincipal principal);

    Page<CourseResponse> getAllPublishedCourses(Pageable pageable);

    Page<CourseResponse> getMyCourses(UserPrincipal principal, Pageable pageable);

    CourseResponse getCourseById(String courseId);

    Boolean isUserOwnerOfCourse(String courseId, UserPrincipal userPrincipal);

    Page<CourseResponse> getCoursesByCategory(String categoryId, Pageable pageable);

    Page<CourseResponse> searchCourses(String keyword, Pageable pageable);

    Page<CourseResponse> getPopularCourses(Pageable pageable);

    Page<CourseResponse> getRecentCourses(Pageable pageable);

    Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> status);

    boolean isPublishedCourseExist(String courseId);
}
