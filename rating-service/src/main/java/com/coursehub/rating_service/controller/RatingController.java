package com.coursehub.rating_service.controller;

import com.coursehub.rating_service.dto.request.RateRequest;
import com.coursehub.rating_service.security.UserPrincipal;
import com.coursehub.rating_service.service.abstracts.IRatingService;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/v1/ratings")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RatingController {

    @Qualifier("courseRatingService")
    IRatingService courseRatingService;

    @Qualifier("instructorRatingService")
    IRatingService instructorRatingService;

    public RatingController(
            @Qualifier("courseRatingService") IRatingService courseRatingService,
            @Qualifier("instructorRatingService") IRatingService instructorRatingService) {
        this.courseRatingService = courseRatingService;
        this.instructorRatingService = instructorRatingService;
    }

    @PostMapping("/rate-course/{courseId}")
    public ResponseEntity<Void> rateCourse(@PathVariable String courseId,
                                           @RequestBody RateRequest request,
                                           @AuthenticationPrincipal UserPrincipal principal) {

        courseRatingService.rate(courseId, request, principal);

        return ok().build();

    }

    @PostMapping("/rate-instructor/{instructorId}")
    public ResponseEntity<Void> rateInstructor(@PathVariable String instructorId,
                                               @RequestBody RateRequest request,
                                               @AuthenticationPrincipal UserPrincipal principal) {

        instructorRatingService.rate(instructorId, request, principal);

        return ok().build();

    }

    @DeleteMapping("/delete-rating-course/{rateId}")
    public ResponseEntity<Void> deleteRateCourse(@PathVariable String rateId,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        courseRatingService.deleteRating(rateId, principal);
        return ok().build();
    }


    @DeleteMapping("/delete-rating-instructor/{rateId}")
    public ResponseEntity<Void> deleteRateInstructor(@PathVariable String rateId,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        instructorRatingService.deleteRating(rateId, principal);
        return ok().build();
    }
}
