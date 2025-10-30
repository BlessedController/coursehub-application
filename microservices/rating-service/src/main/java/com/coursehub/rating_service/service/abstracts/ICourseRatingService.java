package com.coursehub.rating_service.service.abstracts;

import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.rating_service.dto.RateRequest;

public interface ICourseRatingService {
    void rate(String courseId, RateRequest request, UserPrincipal principal);

    void deleteRating(String rateId, UserPrincipal principal);
}
