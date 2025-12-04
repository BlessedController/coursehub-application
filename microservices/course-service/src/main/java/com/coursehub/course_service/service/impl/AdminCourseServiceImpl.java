package com.coursehub.course_service.service.impl;

import com.coursehub.course_service.dto.request.CourseFilterRequest;
import com.coursehub.course_service.dto.response.AdminCourseResponse;
import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.mapper.CourseMapper;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.repository.CourseRepository;
import com.coursehub.course_service.service.AdminCourseService;
import com.coursehub.course_service.specification.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCourseServiceImpl implements AdminCourseService {
    private final CourseRepository courseRepository;


    @Override
    public PageResponse<AdminCourseResponse> getAllCourses(int page, int size) {
        return null;
    }

    @Override
    public AdminCourseResponse getCourseDetailById(String courseId) {
        return null;
    }

    @Override
    public PageResponse<AdminCourseResponse> getAllCoursesByStatus(int page, int size, CourseStatus status) {
        return null;
    }

    @Override
    public void updateCourseStatus(String courseId, CourseStatus status) {

    }

    @Override
    public PageResponse<AdminCourseResponse> filterCourses(int page, int size, CourseFilterRequest filter) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Course> courseSpecification = CourseSpecification.filter(filter);

        Page<Course> result = courseRepository.findAll(courseSpecification, pageable);

        return PageResponse.of(result.map(CourseMapper::toAdminResponse));
    }

}
