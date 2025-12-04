package com.coursehub.commons.security.constants;

public class JwtConstants {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    public static final String ACCESS_TOKEN_TYPE = "ACCESS";
    public static final String REFRESH_TOKEN_TYPE = "REFRESH";
    public static final String INTERNAL_TOKEN_TYPE = "INTERNAL";

    public static final String EXPECTED_ISSUER = "course-hub-identity-service";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLE = "role";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CLAIM_TOKEN_TYPE = "token_type";


    public static final String AUTH_HEADER = "Authorization";
    public static final String INTERNAL_TOKEN_HEADER_NAME = "INTERNAL-X-TOKEN";

    public static final String RATING_SERVICE_APP_NAME = "rating-service";
    public static final String MEDIA_STOCK_SERVICE_APP_NAME = "media-stock-service";
    public static final String IDENTITY_SERVICE_APP_NAME = "identity-service";
    public static final String COURSE_SERVICE_APP_NAME = "course-service";
    public static final String ENROLLMENT_SERVICE_APP_NAME = "enrollment-service";


}
