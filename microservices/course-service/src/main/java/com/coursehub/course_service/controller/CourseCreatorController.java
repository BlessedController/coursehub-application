package com.coursehub.course_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.course_service.dto.request.CreateCourseRequest;
import com.coursehub.course_service.dto.request.UpdateCourseRequest;
import com.coursehub.course_service.dto.response.CreatorCourseResponse;
import com.coursehub.course_service.dto.response.PageResponse;
import com.coursehub.course_service.service.CreatorCourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${course-service.creator-base-url}")
@RequiredArgsConstructor
public class CourseCreatorController {
    private final CreatorCourseService creatorCourseService;

    @PostMapping("/create-course")
    public ResponseEntity<CreatorCourseResponse> createCourse(@AuthenticationPrincipal UserPrincipal principal,
                                                              @Valid @RequestBody CreateCourseRequest request) {
        var response = creatorCourseService.createCourse(principal, request);
        return status(CREATED).body(response);
    }

    @GetMapping("/my-courses")
    public ResponseEntity<PageResponse<CreatorCourseResponse>> getMyCourses(@AuthenticationPrincipal UserPrincipal principal,
                                                                            @RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "rating") String sortBy,
                                                                            @RequestParam(defaultValue = "desc") String orderBy

    ) {
        return status(OK).body(creatorCourseService.getMyCourses(principal, page, size, sortBy, orderBy));
    }

    @PutMapping("/update-course/{id}")
    public ResponseEntity<CreatorCourseResponse> updateCourse(@PathVariable(name = "id") String id,
                                                              @AuthenticationPrincipal UserPrincipal principal,
                                                              @Valid @RequestBody UpdateCourseRequest request) {
        var response = creatorCourseService.updateCourse(id, principal, request);

        return status(OK).body(response);
    }

    @PatchMapping("/publish-course/{id}")
    public ResponseEntity<Void> publishCourse(@PathVariable(name = "id") String id,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        creatorCourseService.publishCourse(id, principal);

        return status(OK).build();
    }

    @DeleteMapping("/delete-course/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable(name = "id") String id,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        creatorCourseService.deleteCourse(id, principal);

        return status(OK).build();
    }

}
