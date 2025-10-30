package com.coursehub.rating_service.publisher;

import com.coursehub.commons.events.rating.AddCourseRatingEvent;
import com.coursehub.commons.events.rating.DeleteCourseRatingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.coursehub.commons.constants.topics.rating.CourseRatingTopics.ADD_COURSE_RATING_TOPIC;
import static com.coursehub.commons.constants.topics.rating.CourseRatingTopics.DELETE_COURSE_RATING_TOPIC;

@Service
public class CourseRatingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CourseRatingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAddCourseRating(AddCourseRatingEvent event) {
        kafkaTemplate.send(ADD_COURSE_RATING_TOPIC, event);
    }


    public void publishDeleteCourseRating(DeleteCourseRatingEvent event) {
        kafkaTemplate.send(DELETE_COURSE_RATING_TOPIC, event);
    }


}
