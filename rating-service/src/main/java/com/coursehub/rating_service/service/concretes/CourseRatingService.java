package com.coursehub.rating_service.service.concretes;

import com.coursehub.rating_service.client.CourseServiceClient;
import com.coursehub.rating_service.dto.request.RateRequest;
import com.coursehub.rating_service.dto.response.DeleteCourseRatingEvent;
import com.coursehub.rating_service.dto.response.RateCourseEvent;
import com.coursehub.rating_service.exception.*;
import com.coursehub.rating_service.model.CourseRating;
import com.coursehub.rating_service.repository.CourseRatingRepository;
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
@Qualifier("courseRatingService")
public class CourseRatingService implements IRatingService {

    CourseRatingRepository courseRatingRepository;
    CourseServiceClient courseServiceClient;
    KafkaTemplate<String, Object> kafkaTemplate;
    static String RATE_COURSE_TOPIC = "rate-course-topic";
    static String DELETE_COURSE_RATING_TOPIC = "delete-course-rating-topic";

    @Override
    public void rate(String targetId, RateRequest request, UserPrincipal principal) {

        if (courseRatingRepository.existsCourseRatingByCourseIdAndUserId(targetId, principal.getId())) {
            throw new AlreadyRatedException("You already rated this course");
        }

        Boolean isCourseExist;
        try {
            isCourseExist = courseServiceClient.isPublishedCourseExist(targetId).getBody();
        } catch (Throwable throwable) {
            isCourseExist = courseServiceClient.isPublishedCourseExistFallBack(targetId, throwable).getBody();
        }

        if (!TRUE.equals(isCourseExist)) {
            throw new NotFoundException("Course not found");
        }





        CourseRating courseRating = CourseRating.builder()
                .courseId(targetId)
                .rating(request.rating())
                .userId(principal.getId())
                .build();

        courseRatingRepository.save(courseRating);

        var rateCourseEvent = RateCourseEvent.builder()
                .courseId(targetId)
                .rating(request.rating())
                .build();

        kafkaTemplate.send(RATE_COURSE_TOPIC, rateCourseEvent);
    }


    @Override
    public void deleteRating(String rateId, UserPrincipal principal) {
        CourseRating rating = courseRatingRepository.findById(rateId).orElseThrow(() ->
                new NotFoundException("Rating not found"));

        if (!Objects.equals(principal.getId(), rating.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }

        courseRatingRepository.delete(rating);

        var deleteCourseRatingEvent = DeleteCourseRatingEvent.builder()
                .courseId(rating.getCourseId())
                .rating(rating.getRating())
                .build();


        kafkaTemplate.send(DELETE_COURSE_RATING_TOPIC, deleteCourseRatingEvent);

    }

}
