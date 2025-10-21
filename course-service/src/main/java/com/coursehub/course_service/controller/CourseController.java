package com.coursehub.course_service.controller;

import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.CourseResponse;
import com.coursehub.course_service.security.UserPrincipal;
import com.coursehub.course_service.service.abstracts.ICourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/v1/courses")
@Validated
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CourseController {

    ICourseService courseService;

    @GetMapping("/all-courses")
    public ResponseEntity<Page<CourseResponse>> getAllPublishedCourses(
            @PageableDefault(sort = "rating", direction = ASC) Pageable pageable
    ) {
        return status(OK).body(courseService.getAllPublishedCourses(pageable));
    }

    @GetMapping("/instructor/my-courses")
    public ResponseEntity<Page<CourseResponse>> getMyCourses(@AuthenticationPrincipal UserPrincipal principal,
                                                             Pageable pageable) {
        return status(OK).body(courseService.getMyCourses(principal, pageable));
    }

    @GetMapping("/instructor/my-course/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable(name = "courseId") String courseId) {
        return status(OK).body(courseService.getCourseById(courseId));
    }

    @PostMapping("/instructor/create-course")
    public ResponseEntity<CourseResponse> createCourse(@AuthenticationPrincipal UserPrincipal principal,
                                                       @Valid @RequestBody CreateCourseRequest request) {
        var response = courseService.createCourse(principal, request);
        return status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/instructor/update-course/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable(name = "id") String id,
                                                       @AuthenticationPrincipal UserPrincipal principal,
                                                       @Valid @RequestBody UpdateCourseRequest request) {
        var response = courseService.updateCourse(id, principal, request);

        return status(OK).body(response);
    }

    @PatchMapping("/instructor/publish-course/{id}")
    public ResponseEntity<Void> publishCourse(@PathVariable(name = "id") String id,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        courseService.publishCourse(id, principal);

        return status(OK).build();
    }

    @DeleteMapping("/instructor/delete-course/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable(name = "id") String id,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        courseService.deleteCourse(id, principal);

        return status(OK).build();
    }


    @GetMapping("/{categoryId}/courses")
    ResponseEntity<Page<CourseResponse>> getCoursesByCategory(
            @PathVariable String categoryId,
            @PageableDefault(sort = "id", direction = ASC) Pageable pageable) {
        var body = courseService.getCoursesByCategory(categoryId, pageable);
        return status(OK).body(body);
    }

    @GetMapping("/search")
    ResponseEntity<Page<CourseResponse>> searchCourses(@RequestParam(defaultValue = "") String keyword,
                                                       @PageableDefault(sort = "title", direction = ASC) Pageable pageable) {
        var body = courseService.searchCourses(keyword, pageable);
        return status(OK).body(body);
    }


    @GetMapping("/popular")
    ResponseEntity<Page<CourseResponse>> getPopularCourses(Pageable pageable) {
        var body = courseService.getPopularCourses(pageable);
        return status(OK).body(body);
    }

    @GetMapping("/new")
    ResponseEntity<Page<CourseResponse>> getNewCourses(Pageable pageable) {
        var body = courseService.getRecentCourses(pageable);
        return status(OK).body(body);
    }

    //todo: identify access level
    @GetMapping("/owner-check/{courseId}")
    ResponseEntity<Boolean> isUserOwnerOfCourse(@PathVariable("courseId") String courseId,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        Boolean isOwner = courseService.isUserOwnerOfCourse(courseId, principal);
        return status(OK).body(isOwner);
    }

    @GetMapping("/is-exist/{courseId}")
    ResponseEntity<Boolean> isPublishedCourseExist(@PathVariable String courseId) {
        boolean body = courseService.isPublishedCourseExist(courseId);

        return status(OK).body(body);

    }

}
