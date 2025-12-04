package com.coursehub.rating_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.rating_service.dto.request.ContentCreatorRatingRequest;
import com.coursehub.rating_service.dto.response.RatingStats;
import com.coursehub.rating_service.service.ContentCreatorRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("${rating-service.content-creator-rating-base-url}")
@RequiredArgsConstructor
public class ContentCreatorRatingController {

    private final ContentCreatorRatingService ratingService;

    @PostMapping
    public ResponseEntity<Void> rateContentCreator(@Valid @RequestBody ContentCreatorRatingRequest request,
                                                   @AuthenticationPrincipal UserPrincipal principal) {


        ratingService.rateContentCreator(principal, request);

        return noContent().build();

    }

    @GetMapping("/average/{contentCreatorId}")
    public ResponseEntity<RatingStats> getAverageContentCreatorRating(@PathVariable String contentCreatorId
    ) {

        var body = ratingService.getAverageContentCreatorRating(contentCreatorId);

        return ok(body);

    }

    @DeleteMapping("/{contentCreatorId}")
    public ResponseEntity<Void> deleteRating(@PathVariable String contentCreatorId,
                                             @AuthenticationPrincipal UserPrincipal principal
    ) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }


        ratingService.deleteRating(contentCreatorId, principal.getId());

        return noContent().build();

    }
}
