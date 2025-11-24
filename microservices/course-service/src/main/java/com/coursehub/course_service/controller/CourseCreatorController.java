package com.coursehub.course_service.controller;

import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.PublicCourseResponse;
import com.coursehub.course_service.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${course-service.creator-base-url}")
@RequiredArgsConstructor
public class CourseCreatorController {
    private final CourseService courseService;

    @GetMapping("/my-courses")
    public ResponseEntity<Page<PublicCourseResponse>> getMyCourses(@AuthenticationPrincipal UserPrincipal principal,
                                                                   Pageable pageable) {
        return status(OK).body(courseService.getMyCourses(principal, pageable));
    }

    @GetMapping("/my-course/{courseId}")
    public ResponseEntity<PublicCourseResponse> getCourseById(@PathVariable(name = "courseId") String courseId) {
        return status(OK).body(courseService.getCourseById(courseId));
    }

    @PostMapping("/create-course")
    public ResponseEntity<PublicCourseResponse> createCourse(@AuthenticationPrincipal UserPrincipal principal,
                                                             @Valid @RequestBody CreateCourseRequest request) {
        var response = courseService.createCourse(principal, request);
        return status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update-course/{id}")
    public ResponseEntity<PublicCourseResponse> updateCourse(@PathVariable(name = "id") String id,
                                                             @AuthenticationPrincipal UserPrincipal principal,
                                                             @Valid @RequestBody UpdateCourseRequest request) {
        var response = courseService.updateCourse(id, principal, request);

        return status(OK).body(response);
    }

    @PatchMapping("/publish-course/{id}")
    public ResponseEntity<Void> publishCourse(@PathVariable(name = "id") String id,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        courseService.publishCourse(id, principal);

        return status(OK).build();
    }

    @DeleteMapping("/delete-course/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable(name = "id") String id,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        courseService.deleteCourse(id, principal);

        return status(OK).build();
    }

}
