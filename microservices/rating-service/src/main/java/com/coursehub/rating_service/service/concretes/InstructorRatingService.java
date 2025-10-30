package com.coursehub.rating_service.service.concretes;

import com.coursehub.commons.events.rating.AddInstructorRatingEvent;
import com.coursehub.commons.events.rating.DeleteInstructorRatingEvent;
import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.rating_service.client.IdentityServiceClient;
import com.coursehub.rating_service.dto.RateRequest;
import com.coursehub.rating_service.model.InstructorRating;
import com.coursehub.rating_service.publisher.InstructorRatingEventPublisher;
import com.coursehub.rating_service.repository.InstructorRatingRepository;
import com.coursehub.rating_service.service.abstracts.IInstructorRatingService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Qualifier("instructorRatingService")
public class InstructorRatingService implements IInstructorRatingService {

    InstructorRatingRepository instructorRatingRepository;
    IdentityServiceClient identityServiceClient;
    InstructorRatingEventPublisher instructorRatingEventPublisher;


    @Override
    public void rate(String targetId, RateRequest request, UserPrincipal principal) {

        if (instructorRatingRepository.existsInstructorRatingByInstructorIdAndUserId(targetId, principal.getId())) {
            throw new AlreadyRatedException("You already rated this author");
        }

        Boolean isInstructorExist = identityServiceClient.isInstructorExist(targetId).getBody();

        if (!TRUE.equals(isInstructorExist)) {
            throw new NotFoundException("Instructor not found");
        }

        InstructorRating instructorRating = InstructorRating.builder()
                .instructorId(targetId)
                .rating(request.rating())
                .userId(principal.getId())
                .build();

        instructorRatingRepository.save(instructorRating);

        var addInstructorRatingEvent = AddInstructorRatingEvent.builder()
                .instructorId(targetId)
                .rating(request.rating())
                .build();

        instructorRatingEventPublisher.publishAddInstructorRating(addInstructorRatingEvent);
    }

    @Override
    public void deleteRating(String rateId, UserPrincipal principal) {

        InstructorRating rating = instructorRatingRepository.findById(rateId).orElseThrow(() ->
                new NotFoundException("Rate not found"));

        if (!Objects.equals(principal.getId(), rating.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }

        instructorRatingRepository.delete(rating);

        var deleteInstructorRatingEvent = DeleteInstructorRatingEvent.builder()
                .instructorId(rating.getInstructorId())
                .rating(rating.getRating())
                .build();

        instructorRatingEventPublisher.publishDeleteInstructorRating(deleteInstructorRatingEvent);

    }
}
