package com.coursehub.identity_service.listener;

import com.coursehub.commons.kafka.events.ContentCreatorRatingUpdatedEvent;
import com.coursehub.identity_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.kafka.topics.InstructorRatingTopics.CONTENT_CREATOR_RATING_UPDATED_TOPIC;

@Component
@RequiredArgsConstructor
public class RatingServiceListener {

    private final InternalService internalService;

    @KafkaListener(
            topics = CONTENT_CREATOR_RATING_UPDATED_TOPIC,
            groupId = "identity-service-group",
            containerFactory = "kafkaUpdateContentCreatorRatingListenerContainerFactory"
    )
    public void listenAddInstructorRatingEvent(ContentCreatorRatingUpdatedEvent event) {
        internalService.updateContentCreatorRating(event);
    }


}
