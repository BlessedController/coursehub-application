package com.coursehub.rating_service.service.abstracts;

import com.coursehub.rating_service.dto.request.RateRequest;
import com.coursehub.rating_service.security.UserPrincipal;

public interface IRatingService {
    void rate(String targetId, RateRequest request, UserPrincipal principal);

    void deleteRating(String rateId, UserPrincipal principal);
}
