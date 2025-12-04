package com.coursehub.media_stock_service.publisher;

import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.coursehub.commons.kafka.topics.VideoTopics.ADD_VIDEO_TO_COURSE_TOPIC;

@Component
@RequiredArgsConstructor
public class KafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAddVideoToCourseEvent(AddVideoToCourseEvent event) {
        kafkaTemplate.send(ADD_VIDEO_TO_COURSE_TOPIC, event);

    }

}

