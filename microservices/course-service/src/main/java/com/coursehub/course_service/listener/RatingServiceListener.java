package com.coursehub.course_service.listener;

import com.coursehub.commons.kafka.events.CourseRatingUpdatedEvent;
import com.coursehub.course_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.kafka.topics.CourseRatingTopics.COURSE_RATING_UPDATED_TOPIC;

@Component
@RequiredArgsConstructor
public class RatingServiceListener {

    private final InternalService internalService;

    @KafkaListener(
            topics = COURSE_RATING_UPDATED_TOPIC,
            groupId = "course-service-group",
            containerFactory = "kafkaUpdateCourseRatingListenerContainerFactory"
    )
    public void listenAddVideoToCourseEvent(CourseRatingUpdatedEvent event) {
        internalService.updateCourseRating(event);
    }


}

