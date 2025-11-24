package com.coursehub.course_service.controller;

import com.coursehub.course_service.dto.request.CourseFilterRequest;
import com.coursehub.course_service.dto.response.*;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${course-service.public-base-url}")
@RequiredArgsConstructor
public class PublicCourseController {

    private final CourseService courseService;

    @GetMapping("/all-courses")
    public ResponseEntity<Page<PublicCourseResponse>> getAllPublishedCourses(
            @PageableDefault(sort = "rating", direction = ASC) Pageable pageable
    ) {
        return status(OK).body(courseService.getAllPublishedCourses(pageable));
    }

    @GetMapping("/{categoryId}/courses")
    ResponseEntity<Page<PublicCourseResponse>> getCoursesByCategory(
            @PathVariable String categoryId,
            @PageableDefault(sort = "id", direction = ASC) Pageable pageable
    ) {
        var body = courseService.getCoursesByCategory(categoryId, pageable);
        return status(OK).body(body);
    }

    @GetMapping("/search")
    ResponseEntity<Page<PublicCourseResponse>> searchCourses(@RequestParam(defaultValue = "") String keyword,
                                                             @PageableDefault(sort = "title", direction = ASC) Pageable pageable) {
        var body = courseService.searchCourses(keyword, pageable);
        return status(OK).body(body);
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<PublicCourseResponse>> filterCourses(
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
                courseService.filterCourses(page, size, filter)
        );
    }

    @GetMapping("/popular")
    ResponseEntity<Page<PublicCourseResponse>> getPopularCourses(Pageable pageable) {
        var body = courseService.getPopularCourses(pageable);
        return status(OK).body(body);
    }

    @GetMapping("/new")
    ResponseEntity<Page<PublicCourseResponse>> getNewCourses(Pageable pageable) {
        var body = courseService.getRecentCourses(pageable);
        return status(OK).body(body);
    }


}
