package com.coursehub.commons.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

public class JwtService {
    private final Key key;
    private final JwtParser parser;
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    public static final String EXPECTED_ISSUER = "course-hub-auth-service";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLE = "role";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes
    public static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    public static final String INTERNAL_TOKEN_HEADER_NAME = "INTERNAL-X-TOKEN";

    public static final String RATING_SERVICE_APP_NAME = "rating-service";
    public static final String MEDIA_STOCK_SERVICE_APP_NAME = "media-stock-service";
    public static final String IDENTITY_SERVICE_APP_NAME = "identity-service";
    public static final String COURSE_SERVICE_APP_NAME = "course-service";


    public JwtService(String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String generateAccessToken(UserPrincipal principal) {
        return buildToken(principal, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserPrincipal principal) {
        return buildToken(principal, REFRESH_TOKEN_EXPIRATION);
    }

    public String generateAccessToken(String email) {
        return buildToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    public String buildToken(String email, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(EXPECTED_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateInternalToken(String serviceName) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(serviceName)
                .setIssuer(serviceName)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 60_000)) // 1 minute
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateInternalToken(String token, String expectedServiceName) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return expectedServiceName.equals(claims.getSubject());
    }

    



    private String buildToken(UserPrincipal principal, long expiration) {

        Claims claims = Jwts.claims().setSubject(principal.getEmail());

        claims.put(CLAIM_USER_ID, principal.getId());
        claims.put(CLAIM_USERNAME, principal.getUsername());
        claims.put(CLAIM_ROLE, principal.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(EXPECTED_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public UserPrincipal verifyAccessToken(HttpServletRequest request) {
        Optional<String> pureToken = this.getPureTokenFromHeaderOrCookie(request);

        if (pureToken.isEmpty()) {
            return null;
        }

        String token = pureToken.get();

        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);

            String email = claims.getBody().getSubject();
            String issuer = claims.getBody().getIssuer();

            String userId = claims.getBody().get(CLAIM_USER_ID, String.class);
            String username = claims.getBody().get(CLAIM_USERNAME, String.class);

            String role = claims.getBody().get(CLAIM_ROLE, String.class);

            if (
                    issuer == null ||
                            !issuer.equals(EXPECTED_ISSUER) ||
                            userId == null ||
                            username == null ||
                            email == null ||
                            role == null
            ) {
                log.error("Missing or invalid claims in token");
                return null;
            }


            return new UserPrincipal(userId, username, email, role);

        } catch (ExpiredJwtException e) {
            log.warn("Access token expired: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    public Optional<String> verifyRefreshToken(String token) {
        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);

            String issuer = claims.getBody().getIssuer();

            if (issuer == null || !issuer.equals(EXPECTED_ISSUER)) {
                log.error("Invalid issuer in refresh token");
                return Optional.empty();
            }

            String email = claims.getBody().getSubject();

            return Optional.of(email);

        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired: {}", e.getMessage());
            return Optional.empty();
        } catch (JwtException e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            return Optional.empty();
        }
    }


    private Optional<String> getPureTokenFromHeaderOrCookie(HttpServletRequest request) {

        String token = request.getHeader(AUTH_HEADER);

        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return Optional.of(token.substring(BEARER_PREFIX.length()));
        }

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ACCESS_TOKEN_COOKIE)) {
                    String value = cookie.getValue();
                    if (value != null && !value.isEmpty()) {
                        return Optional.of(value);
                    }
                }
            }
        }

        return Optional.empty();
    }
}
