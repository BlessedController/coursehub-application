package com.coursehub.course_service.service;

import com.coursehub.course_service.dto.request.CourseFilterRequest;
import com.coursehub.course_service.dto.response.AdminCourseResponse;
import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.model.enums.CourseStatus;

public interface AdminCourseService {

    PageResponse<AdminCourseResponse> getAllCourses(int page, int size);

    AdminCourseResponse getCourseDetailById(String courseId);

    PageResponse<AdminCourseResponse> getAllCoursesByStatus(int page, int size, CourseStatus status);

    void updateCourseStatus(String courseId, CourseStatus status);

    PageResponse<AdminCourseResponse> filterCourses(int page, int size, CourseFilterRequest filter);

}
