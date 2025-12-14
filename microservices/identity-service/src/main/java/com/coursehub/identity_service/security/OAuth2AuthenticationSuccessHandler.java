package com.coursehub.identity_service.security;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.commons.security.service.JwtUserAccessTokenService;
import com.coursehub.commons.security.service.JwtUserRefreshTokenService;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

import static com.coursehub.commons.security.constants.JwtConstants.ACCESS_TOKEN_COOKIE;
import static com.coursehub.commons.security.constants.JwtConstants.REFRESH_TOKEN_COOKIE;
import static com.coursehub.identity_service.constants.JwtTokenConstants.ACCESS_TOKEN_EXPIRATION;
import static com.coursehub.identity_service.constants.JwtTokenConstants.REFRESH_TOKEN_EXPIRATION;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUserAccessTokenService jwtServiceUserAccessToken;
    private final JwtUserRefreshTokenService jwtServiceUserRefreshToken;
    private final UserRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String email = oidcUser.getEmail();

        Supplier<NotFoundException> notFoundExceptionSupplier = () -> new NotFoundException("User not found by email: " + email);

        User user = userRepository.findByEmail(email).orElseThrow(notFoundExceptionSupplier);

        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().toString()
        );

        String accessToken = jwtServiceUserAccessToken.generateAccessToken(principal, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtServiceUserRefreshToken.generateRefreshToken(principal, REFRESH_TOKEN_EXPIRATION);

        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, accessToken)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(ACCESS_TOKEN_EXPIRATION))
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(REFRESH_TOKEN_EXPIRATION))
                .sameSite("Lax")
                .build();


        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        response.setContentType("application/json;charset=UTF-8");

        String redirectUrl = "http://localhost:5173/oauth2/callback?accessToken=" + accessToken + "&refreshToken=" + refreshToken;

        response.sendRedirect(redirectUrl);

    }
}
