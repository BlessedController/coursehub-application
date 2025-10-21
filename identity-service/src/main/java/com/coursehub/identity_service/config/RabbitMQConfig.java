package com.coursehub.identity_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String ADD_INSTRUCTOR_RATING_QUEUE = "rate-instructor-queue";
    public static final String DELETE_INSTRUCTOR_RATING_QUEUE = "delete-rate-instructor-queue";

    public static final String EXCHANGE_NAME = "rating-exchange";

    public static final String ADD_INSTRUCTOR_RATING_ROUTING_KEY = "rate.instructor";
    public static final String DELETE_INSTRUCTOR_RATING_ROUTING_KEY = "delete.rate.instructor";



    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
