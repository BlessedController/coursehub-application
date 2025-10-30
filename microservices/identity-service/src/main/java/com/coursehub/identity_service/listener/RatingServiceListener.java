package com.coursehub.identity_service.listener;

import com.coursehub.commons.events.rating.AddInstructorRatingEvent;
import com.coursehub.commons.events.rating.DeleteInstructorRatingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.constants.topics.rating.InstructorRatingTopics.ADD_INSTRUCTOR_RATING_TOPIC;
import static com.coursehub.commons.constants.topics.rating.InstructorRatingTopics.DELETE_INSTRUCTOR_RATING_TOPIC;

@Component
public class RatingServiceListener {

    @KafkaListener(
            topics = ADD_INSTRUCTOR_RATING_TOPIC,
            groupId = "identity-service-group",
            containerFactory = "kafkaAddInstructorRatingEventListenerContainerFactory"
    )
    public void listenAddInstructorRatingEvent(AddInstructorRatingEvent event) {


    }

    @KafkaListener(
            topics = DELETE_INSTRUCTOR_RATING_TOPIC,
            groupId = "identity-service-group",
            containerFactory = "kafkaDeleteInstructorRatingEventListenerContainerFactory"
    )
    public void listenDeleteInstructorRatingEvent(DeleteInstructorRatingEvent event) {

    }

}
