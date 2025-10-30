package com.coursehub.rating_service.controller;

import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.rating_service.dto.RateRequest;
import com.coursehub.rating_service.service.abstracts.IInstructorRatingService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class InstructorRatingController {

    IInstructorRatingService instructorRatingService;

    @PostMapping("/rate-instructor/{instructorId}")
    public ResponseEntity<Void> rateInstructor(@PathVariable String instructorId,
                                               @RequestBody RateRequest request,
                                               @AuthenticationPrincipal UserPrincipal principal) {

        instructorRatingService.rate(instructorId, request, principal);

        return ok().build();

    }

    @DeleteMapping("/delete-rating-instructor/{rateId}")
    public ResponseEntity<Void> deleteRateInstructor(@PathVariable String rateId,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        instructorRatingService.deleteRating(rateId, principal);
        return ok().build();
    }
}
