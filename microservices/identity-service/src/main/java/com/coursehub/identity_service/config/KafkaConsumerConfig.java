package com.coursehub.identity_service.config;

import com.coursehub.commons.kafka.events.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
public class KafkaConsumerConfig {
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, AddUserProfilePhotoEvent> addUserProfilePhotoEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(AddUserProfilePhotoEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AddUserProfilePhotoEvent> kafkaAddProfilePhotoEventListenerContainerFactory() {
        return buildFactory(AddUserProfilePhotoEvent.class);
    }

    @Bean
    public ConsumerFactory<String, DeleteUserProfilePhotoEvent> deleteUserProfileEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(DeleteUserProfilePhotoEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeleteUserProfilePhotoEvent> kafkaDeleteProfilePhotoEventListenerContainerFactory() {
        return buildFactory(DeleteUserProfilePhotoEvent.class);
    }


    @Bean
    public ConsumerFactory<String, ContentCreatorRatingUpdatedEvent> updateContentCreatorRatingConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(ContentCreatorRatingUpdatedEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ContentCreatorRatingUpdatedEvent> kafkaUpdateContentCreatorRatingListenerContainerFactory() {
        return buildFactory(ContentCreatorRatingUpdatedEvent.class);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildFactory(Class<T> clazz) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
                getConfig(), new StringDeserializer(), buildDeserializer(clazz)
        ));
        return factory;
    }

    private <T> JsonDeserializer<T> buildDeserializer(Class<T> clazz) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("com.coursehub.commons.kafka.events");
        deserializer.setUseTypeMapperForKey(false);
        return deserializer;
    }

    private Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(GROUP_ID_CONFIG, "identity-service-group");
        config.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return config;
    }

}