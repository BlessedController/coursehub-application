package com.coursehub.identity_service.service;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.identity_service.dto.request.UpdateUserInfoRequest;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;

public interface SelfService {

    void updateSelfInfo(UserPrincipal principal, UpdateUserInfoRequest request);

    UserSelfResponse getSelf(UserPrincipal principal);

}
