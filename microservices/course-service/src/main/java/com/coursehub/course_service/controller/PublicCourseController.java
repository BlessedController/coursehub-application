package com.coursehub.course_service.controller;

import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.dto.response.PublicCourseResponse;
import com.coursehub.course_service.service.PublicCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${course-service.public-base-url}")
@RequiredArgsConstructor
public class PublicCourseController {

    private final PublicCourseService publicCourseService;

    @GetMapping("/{id}")
    public ResponseEntity<PublicCourseResponse> getPublishedCourseById(@PathVariable String id) {
        PublicCourseResponse body = publicCourseService.getPublishedCourseById(id);
        return status(OK).body(body);
    }

    @GetMapping("/all-courses")
    public ResponseEntity<PageResponse<PublicCourseResponse>> getAllPublishedCoursesWithFilter(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String orderBy

    ) {
        var body = publicCourseService.getAllPublishedCourses(page, size, sortBy, orderBy);
        return status(OK).body(body);
    }

    @GetMapping("/{categoryId}/courses")
    ResponseEntity<PageResponse<PublicCourseResponse>> getCoursesByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String orderBy
    ) {
        var body = publicCourseService.getCoursesByCategory(categoryId, page, size, sortBy, orderBy);
        return status(OK).body(body);
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<PublicCourseResponse>> filterCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDateTime minTime,
            @RequestParam(required = false) LocalDateTime maxTime,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String orderBy) {

        return ResponseEntity.ok(
                publicCourseService.filterCourses(page, size, keyword, minPrice, maxPrice, minTime, maxTime, minRating, maxRating, categoryId, sortBy, orderBy)
        );
    }

    @GetMapping("/popular-courses")
    ResponseEntity<PageResponse<PublicCourseResponse>> getPopularCourses(@RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "rating") String sortBy,
                                                                         @RequestParam(defaultValue = "desc") String orderBy) {
        var body = publicCourseService.getPopularCourses(page, size, sortBy, orderBy);
        return status(OK).body(body);
    }

    @GetMapping("/recent-courses")
    ResponseEntity<PageResponse<PublicCourseResponse>> getRecentCourses(@RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                        @RequestParam(defaultValue = "desc") String orderBy) {
        var body = publicCourseService.getRecentCourses(page, size, sortBy, orderBy);
        return status(OK).body(body);
    }


}
