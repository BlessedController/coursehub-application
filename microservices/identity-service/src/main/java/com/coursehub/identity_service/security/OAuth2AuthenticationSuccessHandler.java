package com.coursehub.identity_service.security;

import com.coursehub.commons.security.JwtService;
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
import java.util.List;

import static com.coursehub.commons.security.JwtService.REFRESH_TOKEN_COOKIE;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    private static final List<String> ALLOWED_REDIRECTS = List.of(
            "https://your-frontend.com",
            "http://localhost:5173"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();

        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(JwtService.REFRESH_TOKEN_EXPIRATION))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        response.setContentType("application/json;charset=UTF-8");

        String redirectUri = request.getParameter("redirect_uri");

        if (redirectUri != null && ALLOWED_REDIRECTS.stream().anyMatch(redirectUri::startsWith)) {
            response.sendRedirect(redirectUri + "?accessToken=" + accessToken);
            return;
        }

        response.getWriter().write("{\"accessToken\":\"" + accessToken + "\"}");
    }
}
