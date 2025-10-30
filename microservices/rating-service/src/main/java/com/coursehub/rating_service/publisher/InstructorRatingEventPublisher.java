package com.coursehub.rating_service.publisher;

import com.coursehub.commons.events.rating.AddInstructorRatingEvent;
import com.coursehub.commons.events.rating.DeleteInstructorRatingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.coursehub.commons.constants.topics.rating.InstructorRatingTopics.ADD_INSTRUCTOR_RATING_TOPIC;
import static com.coursehub.commons.constants.topics.rating.InstructorRatingTopics.DELETE_INSTRUCTOR_RATING_TOPIC;

@Service
public class InstructorRatingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InstructorRatingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAddInstructorRating(AddInstructorRatingEvent event) {
        kafkaTemplate.send(ADD_INSTRUCTOR_RATING_TOPIC, event);
    }

    public void publishDeleteInstructorRating(DeleteInstructorRatingEvent event) {
        kafkaTemplate.send(DELETE_INSTRUCTOR_RATING_TOPIC, event);
    }

}
