package com.coursehub.course_service.listener;

import com.coursehub.commons.kafka.events.*;
import com.coursehub.course_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.kafka.topics.PhotoTopics.ADD_COURSE_PROFILE_PHOTO_TOPIC;
import static com.coursehub.commons.kafka.topics.PhotoTopics.ADD_VIDEO_PROFILE_PHOTO_TOPIC;
import static com.coursehub.commons.kafka.topics.VideoTopics.ADD_VIDEO_TO_COURSE_TOPIC;
import static com.coursehub.commons.kafka.topics.VideoTopics.DELETE_VIDEO_FROM_COURSE_TOPIC;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MediaServiceListener {

    InternalService internalService;

    @KafkaListener(
            topics = ADD_VIDEO_TO_COURSE_TOPIC,
            groupId = "course-service-group",
            containerFactory = "kafkaAddVideoListenerContainerFactory"
    )
    public void listenAddVideoToCourseEvent(AddVideoToCourseEvent event) {
        log.info("🎬 Received AddVideoToCourseEvent for courseId={}", event.courseId());
        internalService.addVideoToCourse(event);
    }

    @KafkaListener(
            topics = DELETE_VIDEO_FROM_COURSE_TOPIC,
            groupId = "course-service-group",
            containerFactory = "kafkaDeleteVideoListenerContainerFactory"
    )
    public void listenDeleteVideoFromCourseEvent(DeleteVideoFromCourseEvent event) {
        log.info("🗑️ Received DeleteVideoFromCourseEvent for courseId={}", event.courseId());
        internalService.deleteVideoFromCourse(event);
    }

    @KafkaListener(
            topics = ADD_COURSE_PROFILE_PHOTO_TOPIC,
            groupId = "course-service-group",
            containerFactory = "kafkaAddCourseProfilePhotoListenerContainerFactory"
    )
    public void listenAddVideoToCourseEvent(AddPosterPictureToCourseEvent event) {
        log.info("🎬 Received AddProfilePictureToCourseEvent for courseId={}", event.posterPictureName());
        internalService.addProfilePictureToCourse(event);
    }

    @KafkaListener(
            topics = ADD_VIDEO_PROFILE_PHOTO_TOPIC,
            groupId = "course-service-group",
            containerFactory = "kafkaAddVideoProfilePhotoListenerContainerFactory"
    )
    public void listenAddVideoToCourseEvent(AddProfilePictureToVideoEvent event) {
        log.info("🎬 Received AddProfilePictureToVideoEvent for videoId={}", event.videoId());
        internalService.addProfilePictureToVideo(event);
    }


}

