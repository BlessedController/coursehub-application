package com.coursehub.commons.security.service;

import com.coursehub.commons.security.model.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static com.coursehub.commons.security.constants.JwtConstants.*;

public class JwtUserRefreshTokenService {

    private final Key key;
    private final JwtParser parser;

    public JwtUserRefreshTokenService(String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public Optional<String> verifyRefreshToken(String token) {
        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);

            String tokenType = claims.getBody().get(CLAIM_TOKEN_TYPE, String.class);
            if (!REFRESH_TOKEN_TYPE.equals(tokenType)) {
                return Optional.empty();
            }

            String issuer = claims.getBody().getIssuer();
            if (issuer == null || !issuer.equals(EXPECTED_ISSUER)) {
                return Optional.empty();
            }

            String email = claims.getBody().getSubject();
            if (email == null) {
                return Optional.empty();
            }

            return Optional.of(email);

        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    public String generateRefreshToken(UserPrincipal principal, long expiration) {
        return buildRefreshToken(principal.getEmail(), expiration);
    }

    public String generateRefreshToken(String email, long expiration) {
        return buildRefreshToken(email, expiration);
    }

    private String buildRefreshToken(String email, long expiration) {
        long now = System.currentTimeMillis();

        Claims claims = Jwts.claims().setSubject(email);
        claims.put(CLAIM_TOKEN_TYPE, REFRESH_TOKEN_TYPE);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(EXPECTED_ISSUER)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
