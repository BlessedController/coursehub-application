package com.coursehub.identity_service.security;

import jakarta.servlet.http.HttpServletRequest;

public interface IJwtService {

    String generateAccessToken(UserPrincipal principal);

    UserPrincipal verifyAccessToken(HttpServletRequest request);

}
