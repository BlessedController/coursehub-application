package com.coursehub.rating_service.controller;

import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.rating_service.dto.RateRequest;
import com.coursehub.rating_service.service.abstracts.ICourseRatingService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/v1/ratings")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CourseRatingController {

    ICourseRatingService courseRatingService;

    @PostMapping("/courses/{courseId}/ratings")
    public ResponseEntity<Void> rateCourse(@PathVariable String courseId,
                                           @RequestBody RateRequest request,
                                           @AuthenticationPrincipal UserPrincipal principal) {

        courseRatingService.rate(courseId, request, principal);

        return ok().build();

    }

    @DeleteMapping("/courses/ratings/{rateId}")
    public ResponseEntity<Void> deleteRateCourse(@PathVariable String rateId,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        courseRatingService.deleteRating(rateId, principal);
        return ok().build();
    }

}
