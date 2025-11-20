package com.coursehub.identity_service.config;

import com.coursehub.commons.kafka.events.AddUserProfilePhotoEvent;
import com.coursehub.commons.kafka.events.DeleteUserProfilePhotoEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, AddUserProfilePhotoEvent> addUserProfilePhotoEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(AddUserProfilePhotoEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AddUserProfilePhotoEvent> kafkaAddProfilePhotoEventListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, AddUserProfilePhotoEvent>();
        factory.setConsumerFactory(addUserProfilePhotoEventConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, DeleteUserProfilePhotoEvent> deleteUserProfileEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfig(), new StringDeserializer(), buildDeserializer(DeleteUserProfilePhotoEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeleteUserProfilePhotoEvent> kafkaDeleteProfilePhotoEventListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DeleteUserProfilePhotoEvent>();
        factory.setConsumerFactory(deleteUserProfileEventConsumerFactory());
        return factory;
    }

    private <T> JsonDeserializer<T> buildDeserializer(Class<T> clazz) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);
        return deserializer;
    }

    private Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(BOOTSTRAP_SERVERS_CONFIG, System.getenv().getOrDefault("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
        config.put(GROUP_ID_CONFIG, "identity-service-group");
        config.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return config;
    }

}