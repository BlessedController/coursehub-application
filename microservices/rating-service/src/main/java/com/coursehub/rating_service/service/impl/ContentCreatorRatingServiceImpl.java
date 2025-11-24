package com.coursehub.rating_service.service.impl;

import com.coursehub.commons.exceptions.ConflictException;
import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.kafka.events.ContentCreatorRatingUpdatedEvent;
import com.coursehub.rating_service.dto.request.ContentCreatorRatingRequest;
import com.coursehub.rating_service.dto.response.RatingStats;
import com.coursehub.rating_service.model.ContentCreatorRating;
import com.coursehub.rating_service.repository.ContentCreatorRatingRepository;
import com.coursehub.rating_service.service.ContentCreatorRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.coursehub.commons.kafka.topics.InstructorRatingTopics.CONTENT_CREATOR_RATING_UPDATED_TOPIC;

@Service
@RequiredArgsConstructor
public class ContentCreatorRatingServiceImpl implements ContentCreatorRatingService {

    private final ContentCreatorRatingRepository contentCreatorRatingRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void rateContentCreator(String userId, ContentCreatorRatingRequest request) {

        if (Objects.equals(userId, request.contentCreatorId())) {
            throw new ConflictException("Content creator and user cannot be the same.");
        }

        if (contentCreatorRatingRepository.existsByUserIdAndContentCreatorId(userId, request.contentCreatorId())) {
            throw new ConflictException("Already existing user rating");
        }

        double rating = Math.round(request.rating() * 10.0) / 10.0;

        ContentCreatorRating contentCreatorRating = ContentCreatorRating.builder()
                .userId(userId)
                .contentCreatorId(request.contentCreatorId())
                .rating(rating)
                .build();

        contentCreatorRatingRepository.save(contentCreatorRating);


        RatingStats stats = this.findRatingStats(request.contentCreatorId());

        var event = ContentCreatorRatingUpdatedEvent.builder()
                .instructorId(contentCreatorRating.getContentCreatorId())
                .averageRating(stats.averageRating())
                .ratingCount((int) stats.ratingCount())
                .build();

        kafkaTemplate.send(CONTENT_CREATOR_RATING_UPDATED_TOPIC, event);
    }

    @Override
    public RatingStats getAverageContentCreatorRating(String contentCreatorId) {
        return this.findRatingStats(contentCreatorId);
    }

    @Override
    public void deleteRating(String contentCreatorId, String userId) {
        ContentCreatorRating byCourseIdAndUserId = contentCreatorRatingRepository.findByContentCreatorIdAndUserId(contentCreatorId, userId)
                .orElseThrow(() -> new NotFoundException("ContentCreatorRating not found")
                );

        contentCreatorRatingRepository.delete(byCourseIdAndUserId);

        RatingStats stats = this.findRatingStats(contentCreatorId);

        var event = ContentCreatorRatingUpdatedEvent.builder()
                .instructorId(contentCreatorId)
                .averageRating(stats.averageRating())
                .ratingCount((int) stats.ratingCount())
                .build();

        kafkaTemplate.send(CONTENT_CREATOR_RATING_UPDATED_TOPIC, event);
    }


    private RatingStats findRatingStats(String contentCreatorId) {
        RatingStats stats = contentCreatorRatingRepository.findRatingStats(contentCreatorId);

        if (stats == null || stats.ratingCount() == 0) {
            return new RatingStats(0, 0.0);
        }

        Double avg = stats.averageRating();

        if (avg == null) avg = 0.0;

        double rounded = Math.round(avg * 10.0) / 10.0;

        return new RatingStats(stats.ratingCount(), rounded);
    }

}
