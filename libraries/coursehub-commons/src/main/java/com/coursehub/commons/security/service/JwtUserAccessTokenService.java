package com.coursehub.commons.security.service;

import com.coursehub.commons.security.model.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static com.coursehub.commons.security.constants.JwtConstants.*;

public class JwtUserAccessTokenService {

    private final Key key;
    private final JwtParser parser;

    public JwtUserAccessTokenService(String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public UserPrincipal verifyAccessToken(HttpServletRequest request) {

        Optional<String> tokenOpt = this.getPureUserAccessTokenFromRequestOrCookie(request);

        if (tokenOpt.isEmpty()) return null;

        try {
            String token = tokenOpt.get();
            Jws<Claims> claims = parser.parseClaimsJws(token);

            String tokenType = claims.getBody().get(CLAIM_TOKEN_TYPE, String.class);
            if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
                return null;
            }

            String issuer = claims.getBody().getIssuer();
            if (issuer == null || !issuer.equals(EXPECTED_ISSUER)) {
                return null;
            }

            String email = claims.getBody().getSubject();
            String userId = claims.getBody().get(CLAIM_USER_ID, String.class);
            String username = claims.getBody().get(CLAIM_USERNAME, String.class);
            String role = claims.getBody().get(CLAIM_ROLE, String.class);

            if (email == null || userId == null || username == null || role == null) {
                return null;
            }

            return new UserPrincipal(userId, username, email, role);

        } catch (JwtException e) {
            return null;
        }
    }

    public String generateAccessToken(UserPrincipal principal, long expiration) {
        Claims claims = Jwts.claims().setSubject(principal.getEmail());

        claims.put(CLAIM_USER_ID, principal.getId());
        claims.put(CLAIM_USERNAME, principal.getUsername());
        claims.put(CLAIM_ROLE, principal.getRole().name());
        claims.put(CLAIM_TOKEN_TYPE, ACCESS_TOKEN_TYPE);

        return build(claims, expiration);
    }

    private String build(Claims claims, long expiration) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(EXPECTED_ISSUER)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Optional<String> getPureUserAccessTokenFromRequestOrCookie(HttpServletRequest request) {

        String token = request.getHeader(AUTH_HEADER);

        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return Optional.of(token.substring(BEARER_PREFIX.length()));
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
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
