package com.coursehub.media_stock_service.publisher;

import com.coursehub.commons.kafka.events.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.kafka.topics.PhotoTopics.*;
import static com.coursehub.commons.kafka.topics.VideoTopics.ADD_VIDEO_TO_COURSE_TOPIC;

@Component
@RequiredArgsConstructor
public class KafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

    public void publishEvent(AddProfilePictureToUserEvent event) {
        kafkaTemplate.send(ADD_USER_PROFILE_PHOTO_TOPIC, event.userId(), event);
        log.info("Message sent to topic: {}", ADD_USER_PROFILE_PHOTO_TOPIC);
    }


    public void publishEvent(AddProfilePictureToCourseEvent event) {
        kafkaTemplate.send(ADD_COURSE_PROFILE_PHOTO_TOPIC, event.courseId(), event);
        log.info("Message sent to topic: {}", ADD_COURSE_PROFILE_PHOTO_TOPIC);
    }

    public void publishEvent(AddProfilePictureToVideoEvent event) {
        kafkaTemplate.send(ADD_VIDEO_PROFILE_PHOTO_TOPIC, event.videoId(), event);
        log.info("Message sent to topic: {}", ADD_VIDEO_PROFILE_PHOTO_TOPIC);
    }


    public void publishEvent(AddVideoToCourseEvent event) {
        kafkaTemplate.send(ADD_VIDEO_TO_COURSE_TOPIC, event);
        log.info("Message sent to topic: {}", ADD_VIDEO_TO_COURSE_TOPIC);
    }

}

