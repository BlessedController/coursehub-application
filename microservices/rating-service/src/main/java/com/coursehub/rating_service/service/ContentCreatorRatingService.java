package com.coursehub.rating_service.service;

import com.coursehub.rating_service.dto.request.ContentCreatorRatingRequest;
import com.coursehub.rating_service.dto.response.RatingStats;


public interface ContentCreatorRatingService {

    void rateContentCreator( String userId, ContentCreatorRatingRequest request );

    RatingStats getAverageContentCreatorRating(String contentCreatorId);

    void deleteRating(String contentCreatorId, String userId);

}
