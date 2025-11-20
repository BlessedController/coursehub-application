package com.coursehub.course_service.config;

import com.coursehub.commons.events.media.AddVideoToCourseEvent;
import com.coursehub.commons.events.media.DeleteVideoFromCourseEvent;
import com.coursehub.commons.events.rating.AddCourseRatingEvent;
import com.coursehub.commons.events.rating.DeleteCourseRatingEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, AddVideoToCourseEvent> addVideoConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(AddVideoToCourseEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AddVideoToCourseEvent> kafkaAddVideoListenerContainerFactory() {
        return buildFactory(AddVideoToCourseEvent.class);
    }

    @Bean
    public ConsumerFactory<String, DeleteVideoFromCourseEvent> deleteVideoConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(DeleteVideoFromCourseEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeleteVideoFromCourseEvent> kafkaDeleteVideoListenerContainerFactory() {
        return buildFactory(DeleteVideoFromCourseEvent.class);

    }

    @Bean
    public ConsumerFactory<String, AddCourseRatingEvent> addCourseRatingConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(AddCourseRatingEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AddCourseRatingEvent> kafkaAddCourseRatingListenerContainerFactory() {
        return buildFactory(AddCourseRatingEvent.class);
    }

    @Bean
    public ConsumerFactory<String, DeleteCourseRatingEvent> deleteCourseRatingConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(DeleteCourseRatingEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeleteCourseRatingEvent> kafkaDeleteCourseRatingListenerContainerFactory() {
        return buildFactory(DeleteCourseRatingEvent.class);
    }


    private <T> JsonDeserializer<T> buildDeserializer(Class<T> clazz) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("com.coursehub.commons.events");
        deserializer.setUseTypeMapperForKey(false);
        return deserializer;
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildFactory(Class<T> clazz) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
                getConfig(), new StringDeserializer(), buildDeserializer(clazz)
        ));
        return factory;
    }


    private Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(BOOTSTRAP_SERVERS_CONFIG, System.getenv().getOrDefault("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
        config.put(GROUP_ID_CONFIG, "course-service-group");
        config.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return config;
    }
}
