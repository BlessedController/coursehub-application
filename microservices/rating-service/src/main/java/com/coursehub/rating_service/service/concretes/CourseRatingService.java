package com.coursehub.rating_service.service.concretes;

import com.coursehub.commons.events.rating.AddCourseRatingEvent;
import com.coursehub.commons.events.rating.DeleteCourseRatingEvent;
import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.rating_service.client.CourseServiceClient;
import com.coursehub.rating_service.dto.RateRequest;
import com.coursehub.rating_service.model.CourseRating;
import com.coursehub.rating_service.publisher.CourseRatingEventPublisher;
import com.coursehub.rating_service.repository.CourseRatingRepository;
import com.coursehub.rating_service.service.abstracts.ICourseRatingService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CourseRatingService implements ICourseRatingService {

    CourseRatingRepository courseRatingRepository;
    CourseServiceClient courseServiceClient;
    CourseRatingEventPublisher courseRatingEventPublisher;

    @Override
    public void rate(String courseId, RateRequest request, UserPrincipal principal) {

        if (courseRatingRepository.existsCourseRatingByCourseIdAndUserId(courseId, principal.getId())) {
            throw new AlreadyRatedException("You already rated this course");
        }

        Boolean isCourseExist = courseServiceClient.isPublishedCourseExist(courseId).getBody();

        if (!TRUE.equals(isCourseExist)) {
            throw new NotFoundException("Course not found");
        }

        CourseRating courseRating = CourseRating.builder()
                .courseId(courseId)
                .rating(request.rating())
                .userId(principal.getId())
                .build();

        courseRatingRepository.save(courseRating);

        var addCourseRatingEvent = AddCourseRatingEvent.builder()
                .courseId(courseId)
                .rating(request.rating())
                .build();

        courseRatingEventPublisher.publishAddCourseRating(addCourseRatingEvent);
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

        courseRatingEventPublisher.publishDeleteCourseRating(deleteCourseRatingEvent);
    }

}
