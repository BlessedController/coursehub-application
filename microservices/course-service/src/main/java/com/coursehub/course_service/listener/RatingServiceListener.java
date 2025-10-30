package com.coursehub.course_service.listener;

import com.coursehub.commons.events.rating.AddCourseRatingEvent;
import com.coursehub.commons.events.rating.DeleteCourseRatingEvent;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.constants.topics.rating.CourseRatingTopics.ADD_COURSE_RATING_TOPIC;
import static com.coursehub.commons.constants.topics.rating.CourseRatingTopics.DELETE_COURSE_RATING_TOPIC;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RatingServiceListener {

    @KafkaListener(
            topics = ADD_COURSE_RATING_TOPIC,
            groupId = "course-service-group",
            containerFactory = "kafkaAddCourseRatingListenerContainerFactory"
    )
    public void listenAddCourseRatingEvent(AddCourseRatingEvent event) {
        log.info("⭐ Received AddCourseRatingEvent for courseId={} | rating={}", event.courseId(), event.rating());
        // TODO: handle rating addition
    }

    @KafkaListener(
            topics = DELETE_COURSE_RATING_TOPIC,
            groupId = "course-service-group",
            containerFactory = "kafkaDeleteCourseRatingListenerContainerFactory"
    )
    public void listenDeleteCourseRatingEvent(DeleteCourseRatingEvent event) {
        log.info("🗑️ Received DeleteCourseRatingEvent for courseId={} | rating={}", event.courseId(), event.rating());
        // TODO: handle rating deletion
    }

}
