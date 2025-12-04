package com.coursehub.course_service.controller;

import com.coursehub.course_service.dto.request.CourseFilterRequest;
import com.coursehub.course_service.dto.response.AdminCourseResponse;
import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.service.AdminCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("${course-service.admin-base-url}")
@RequiredArgsConstructor
public class AdminController {

    private final AdminCourseService adminCourseService;

    @GetMapping
    public ResponseEntity<PageResponse<AdminCourseResponse>> getAllCourses(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(adminCourseService.getAllCourses(page, size));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<AdminCourseResponse> getCourseDetailById(@PathVariable String courseId) {
        return ResponseEntity.ok(adminCourseService.getCourseDetailById(courseId));
    }

    @GetMapping("/all-by-status")
    public ResponseEntity<PageResponse<AdminCourseResponse>> getAllCoursesByStatus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CourseStatus status
    ) {
        return ResponseEntity.ok(adminCourseService.getAllCoursesByStatus(page, size, status));
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<AdminCourseResponse>> filterCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDateTime minTime,
            @RequestParam(required = false) LocalDateTime maxTime,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) String category
    ) {

        CourseFilterRequest filter = new CourseFilterRequest(
                status, keyword, minPrice, maxPrice,
                minTime, maxTime, minRating, maxRating, category
        );

        return ResponseEntity.ok(
                adminCourseService.filterCourses(page, size, filter)
        );
    }


    @PostMapping("/{courseId}/status")
    public ResponseEntity<Void> updateCourseStatus(
            @PathVariable String courseId,
            @RequestParam CourseStatus status
    ) {
        adminCourseService.updateCourseStatus(courseId, status);
        return ResponseEntity.ok().build();
    }


}
