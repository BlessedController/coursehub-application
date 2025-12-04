package com.coursehub.rating_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.kafka.events.CourseRatingUpdatedEvent;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.rating_service.client.CourseServiceClient;
import com.coursehub.rating_service.dto.request.CourseRatingRequest;
import com.coursehub.rating_service.dto.response.RatingStats;
import com.coursehub.rating_service.model.CourseRating;
import com.coursehub.rating_service.repository.CourseRatingRepository;
import com.coursehub.rating_service.service.CourseRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.coursehub.commons.kafka.topics.CourseRatingTopics.COURSE_RATING_UPDATED_TOPIC;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class CourseRatingServiceImpl implements CourseRatingService {

    private final CourseRatingRepository courseRatingRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CourseServiceClient courseServiceClient;


    @Override
    public void rateCourse(UserPrincipal principal, CourseRatingRequest request) {

        this.validateisPublishedCourseExist(request.courseId());

        this.isUserOwnerOfCourse(principal.getId(), request.courseId());

        this.validateRatingAlreadyExist(request.courseId(), request.courseId());

        double rating = Math.round(request.rating() * 10.0) / 10.0;

        CourseRating courseRating = CourseRating.builder()
                .userId(principal.getId())
                .courseId(request.courseId())
                .rating(rating)
                .build();

        courseRatingRepository.save(courseRating);

        RatingStats stats = this.findRatingStats(request.courseId());

        var event = CourseRatingUpdatedEvent.builder()
                .courseId(courseRating.getCourseId())
                .averageRating(stats.averageRating())
                .ratingCount((int) stats.ratingCount())
                .build();

        kafkaTemplate.send(COURSE_RATING_UPDATED_TOPIC, event);

    }

    @Override
    public void deleteRating(String courseID, String userId) {

        CourseRating byCourseIdAndUserId = courseRatingRepository.findByCourseIdAndUserId(courseID, userId)
                .orElseThrow(() -> new NotFoundException("Course rating not found"));

        courseRatingRepository.delete(byCourseIdAndUserId);

        RatingStats stats = this.findRatingStats(courseID);

        var event = CourseRatingUpdatedEvent.builder()
                .courseId(courseID)
                .averageRating(stats.averageRating())
                .ratingCount((int) stats.ratingCount())
                .build();

        kafkaTemplate.send(COURSE_RATING_UPDATED_TOPIC, event);

    }

    @Override
    public RatingStats getAverageCourseRating(String courseId) {
        return this.findRatingStats(courseId);
    }

    private void validateisPublishedCourseExist(String courseId) {
        Boolean isPublishedCourseExist = courseServiceClient.isPublishedCourseExist(courseId).getBody();

        if (TRUE.equals(isPublishedCourseExist)) {
            throw new InvalidRequestException("Course does not exist");
        }
    }

    private void isUserOwnerOfCourse(String currentUserId, String courseId) {
        Boolean isUserOwnerOfCourse = courseServiceClient.isUserOwnerOfCourse(currentUserId, courseId).getBody();

        if (TRUE.equals(isUserOwnerOfCourse)) {
            throw new InvalidRequestException("You are owner of this course");
        }
    }

    private void validateRatingAlreadyExist(String currentUserId, String courseId) {
        if (courseRatingRepository.existsByUserIdAndCourseId(currentUserId, courseId)) {
            throw new ConflictException("Already existing course rating");
        }
    }


    private RatingStats findRatingStats(String courseId) {
        RatingStats stats = courseRatingRepository.findRatingStats(courseId);

        if (stats == null || stats.ratingCount() == 0) {
            return new RatingStats(0, 0.0);
        }

        Double avg = stats.averageRating();

        if (avg == null) avg = 0.0;

        double rounded = Math.round(avg * 10.0) / 10.0;

        return new RatingStats(stats.ratingCount(), rounded);
    }


}