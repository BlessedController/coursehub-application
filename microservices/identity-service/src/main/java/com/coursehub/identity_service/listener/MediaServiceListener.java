package com.coursehub.identity_service.listener;

import com.coursehub.commons.kafka.events.AddUserProfilePhotoEvent;
import com.coursehub.commons.kafka.events.DeleteUserProfilePhotoEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.kafka.topics.PhotoTopics.ADD_PROFILE_PHOTO_TOPIC;
import static com.coursehub.commons.kafka.topics.PhotoTopics.DELETE_PROFILE_PHOTO_TOPIC;


@Component
public class MediaServiceListener {

    @KafkaListener(
            topics = ADD_PROFILE_PHOTO_TOPIC,
            groupId = "identity-service-group",
            containerFactory = "kafkaAddProfilePhotoEventListenerContainerFactory"
    )
    public void listenAddInstructorRatingEvent(AddUserProfilePhotoEvent event) {


    }

    @KafkaListener(
            topics = DELETE_PROFILE_PHOTO_TOPIC,
            groupId = "identity-service-group",
            containerFactory = "kafkaDeleteProfilePhotoEventListenerContainerFactory"
    )
    public void listenDeleteInstructorRatingEvent(DeleteUserProfilePhotoEvent event) {

    }

}
