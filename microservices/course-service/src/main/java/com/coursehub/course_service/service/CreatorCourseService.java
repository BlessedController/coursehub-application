package com.coursehub.course_service.service;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.CreatorCourseResponse;
import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;

import java.util.Set;

public interface CreatorCourseService {
    CreatorCourseResponse createCourse(UserPrincipal principal, CreateCourseRequest request);

    CreatorCourseResponse updateCourse(String id, UserPrincipal principal, UpdateCourseRequest request);

    void publishCourse(String id, UserPrincipal principal);

    void deleteCourse(String id, UserPrincipal principal);

    PageResponse<CreatorCourseResponse> getMyCourses(UserPrincipal principal, int page, int size, String sortBy, String orderBy);

    Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> status);

}
