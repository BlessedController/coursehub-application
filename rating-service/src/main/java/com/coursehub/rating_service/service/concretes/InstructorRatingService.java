package com.coursehub.rating_service.service.concretes;

import com.coursehub.rating_service.client.IdentityServiceClient;
import com.coursehub.rating_service.dto.request.RateRequest;
import com.coursehub.rating_service.dto.response.AddInstructorRatingEvent;
import com.coursehub.rating_service.dto.response.DeleteInstructorRatingEvent;
import com.coursehub.rating_service.exception.*;
import com.coursehub.rating_service.model.InstructorRating;
import com.coursehub.rating_service.repository.InstructorRatingRepository;
import com.coursehub.rating_service.security.UserPrincipal;
import com.coursehub.rating_service.service.abstracts.IRatingService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Qualifier("instructorRatingService")
public class InstructorRatingService implements IRatingService {

    InstructorRatingRepository instructorRatingRepository;
    IdentityServiceClient identityServiceClient;
    KafkaTemplate<String, Object> kafkaTemplate;

    static String ADD_INSTRUCTOR_RATING_TOPIC = "add-instructor-rating-topic";
    static String DELETE_INSTRUCTOR_RATING_TOPIC = "delete-instructor-rating-topic";


    @Override
    public void rate(String targetId, RateRequest request, UserPrincipal principal) {

        if(instructorRatingRepository.existsInstructorRatingByInstructorIdAndUserId(targetId, principal.getId())){
            throw new AlreadyRatedException("You already rated this author");
        }

        Boolean isInstructorExist;
        try {
            isInstructorExist = identityServiceClient.isInstructorExist(targetId).getBody();
        } catch (Throwable throwable) {
            isInstructorExist = identityServiceClient.isInstructorExistFallBack(targetId, throwable).getBody();
        }

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

        kafkaTemplate.send(ADD_INSTRUCTOR_RATING_TOPIC, addInstructorRatingEvent);

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

        kafkaTemplate.send(DELETE_INSTRUCTOR_RATING_TOPIC, deleteInstructorRatingEvent);

    }
}
