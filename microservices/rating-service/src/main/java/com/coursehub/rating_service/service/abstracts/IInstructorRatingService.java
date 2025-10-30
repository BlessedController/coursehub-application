package com.coursehub.rating_service.service.abstracts;

import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.rating_service.dto.RateRequest;

public interface IInstructorRatingService {
    void rate(String targetId, RateRequest request, UserPrincipal principal);

    void deleteRating(String rateId, UserPrincipal principal);
}
