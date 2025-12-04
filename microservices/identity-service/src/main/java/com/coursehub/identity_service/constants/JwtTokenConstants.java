package com.coursehub.identity_service.constants;

public class JwtTokenConstants {

    private JwtTokenConstants() {
    }

    public static final long ACCESS_TOKEN_EXPIRATION = 2 * 3_600_000L;
    public static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 3_600_000L;

}
