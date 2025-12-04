package com.coursehub.rating_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.rating_service.dto.request.CourseRatingRequest;
import com.coursehub.rating_service.dto.response.RatingStats;
import com.coursehub.rating_service.service.CourseRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("${rating-service.course-rating-base-url}")
@RequiredArgsConstructor
public class CourseRatingController {

    private final CourseRatingService courseRatingService;

    @PostMapping
    public ResponseEntity<Void> rateCourse(@Valid @RequestBody CourseRatingRequest request,
                                           @AuthenticationPrincipal UserPrincipal principal) {

        courseRatingService.rateCourse(principal, request);

        return noContent().build();
    }

    @GetMapping("/average/{courseId}")
    public ResponseEntity<RatingStats> getAverage(@PathVariable String courseId) {
        var body = courseRatingService.getAverageCourseRating(courseId);
        return ok(body);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteRating(@PathVariable String courseId,
                                             @AuthenticationPrincipal UserPrincipal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        courseRatingService.deleteRating(courseId, principal.getId());
        return noContent().build();
    }
}
