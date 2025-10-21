package com.coursehub.course_service.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String ADD_COURSE_RATING_QUEUE = "rate-course-queue";
    public static final String DELETE_COURSE_RATING_QUEUE = "delete-rate-course-queue";

    public static final String EXCHANGE_NAME = "rating-exchange";

    public static final String ADD_COURSE_RATING_ROUTING_KEY = "rate.course";
    public static final String DELETE_COURSE_RATING_ROUTING_KEY = "delete.rate.course";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
