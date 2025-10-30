package com.coursehub.identity_service.constants;

public class JwtTokenConstants {

    private JwtTokenConstants() {
    }

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String EXPECTED_ISSUER = "course-hub-identity-service";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLE = "role";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 15 * 4 * 2;         // 2 saat

}
