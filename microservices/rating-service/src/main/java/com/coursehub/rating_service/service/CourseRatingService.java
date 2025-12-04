package com.coursehub.rating_service.service;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.rating_service.dto.request.CourseRatingRequest;
import com.coursehub.rating_service.dto.response.RatingStats;


public interface CourseRatingService {

    void rateCourse(UserPrincipal principal, CourseRatingRequest request);

    RatingStats getAverageCourseRating(String courseID);

    void deleteRating(String courseID, String userID);



}
