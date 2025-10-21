package com.coursehub.rating_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JwtService {
    Key key;
    JwtParser parser;

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String EXPECTED_ISSUER = "course-hub-identity-service";
    private static final String CLAIM_USER_ID = "user_id";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    public JwtService(@Value("${services.identity.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
    }


    public UserPrincipal verifyAccessToken(HttpServletRequest request) {
        Optional<String> pureToken = resolveToken(request);

        if (pureToken.isEmpty()) {
            return null;
        }
        String token = pureToken.get();

        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);

            String username = claims.getBody().getSubject();
            String issuer = claims.getBody().getIssuer();

            String userId = claims.getBody().get(CLAIM_USER_ID, String.class);
            String email = claims.getBody().get(CLAIM_EMAIL, String.class);
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

    //return pure token or null
    private Optional<String> resolveToken(HttpServletRequest request) {

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
