package com.coursehub.commons.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.*;

import static com.coursehub.commons.security.constants.JwtConstants.*;

public class JwtInternalTokenService {

    private final Key key;
    private final JwtParser parser;

    private static final Set<String> ALLOWED_SERVICES = Set.of(
            RATING_SERVICE_APP_NAME,
            MEDIA_STOCK_SERVICE_APP_NAME,
            IDENTITY_SERVICE_APP_NAME,
            COURSE_SERVICE_APP_NAME,
            ENROLLMENT_SERVICE_APP_NAME
    );

    public JwtInternalTokenService(String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String generateInternalToken(String serviceName) {
        long now = System.currentTimeMillis();

        Claims claims = Jwts.claims().setSubject(serviceName);
        claims.put(CLAIM_TOKEN_TYPE, INTERNAL_TOKEN_TYPE);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(EXPECTED_ISSUER)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + (10 * 60_000)))   // 10 min
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean verifyInternalToken(HttpServletRequest request) {

        Optional<String> tokenOpt = this.getPureInternalTokenFromHeader(request);

        if (tokenOpt.isEmpty()) return false;

        String token = tokenOpt.get();

        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);

            String serviceName = claims.getBody().getSubject();
            String issuer = claims.getBody().getIssuer();
            String tokenType = claims.getBody().get(CLAIM_TOKEN_TYPE, String.class);

            return EXPECTED_ISSUER.equals(issuer)
                    && INTERNAL_TOKEN_TYPE.equals(tokenType)
                    && ALLOWED_SERVICES.contains(serviceName);

        } catch (JwtException e) {
            return false;
        }
    }

    private Optional<String> getPureInternalTokenFromHeader(HttpServletRequest request) {

        String token = request.getHeader(INTERNAL_TOKEN_HEADER_NAME);

        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return Optional.of(token.substring(BEARER_PREFIX.length()));
        }

        return Optional.empty();
    }
}
