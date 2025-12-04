package com.coursehub.identity_service.listener;

import com.coursehub.commons.kafka.events.AddUserProfilePhotoEvent;
import com.coursehub.commons.kafka.events.DeleteUserProfilePhotoEvent;
import com.coursehub.identity_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.kafka.topics.PhotoTopics.ADD_PROFILE_PHOTO_TOPIC;
import static com.coursehub.commons.kafka.topics.PhotoTopics.DELETE_PROFILE_PHOTO_TOPIC;

//TODO: who knows will work? idk what do i have to do? run together with media-stock ms;
@Component
@RequiredArgsConstructor
public class MediaServiceListener {
    private final InternalService internalService;

    @KafkaListener(
            topics = ADD_PROFILE_PHOTO_TOPIC,
            groupId = "identity-service-group",
            containerFactory = "kafkaAddProfilePhotoEventListenerContainerFactory"
    )
    public void listenAddUserProfilePhotoEvent(AddUserProfilePhotoEvent event) {
        internalService.addUserProfilePhotoEvent(event);
    }

    @KafkaListener(
            topics = DELETE_PROFILE_PHOTO_TOPIC,
            groupId = "identity-service-group",
            containerFactory = "kafkaDeleteProfilePhotoEventListenerContainerFactory"
    )
    public void listenDeleteUserProfilePhotoEvent(DeleteUserProfilePhotoEvent event) {
        internalService.deleteUserProfilePhotoEvent(event);
    }

}
