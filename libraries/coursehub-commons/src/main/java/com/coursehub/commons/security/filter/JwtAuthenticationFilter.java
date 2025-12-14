package com.coursehub.commons.security.filter;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.commons.security.service.JwtUserAccessTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUserAccessTokenService jwtUserAccessTokenService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    public JwtAuthenticationFilter(JwtUserAccessTokenService jwtUserAccessTokenService,
                                   HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtUserAccessTokenService = jwtUserAccessTokenService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {

        try {
            String path = request.getRequestURI();

            if (path.contains("/internal/")) {
                filterChain.doFilter(request, response);
                return;
            }
            UserPrincipal currentUserPrincipal = jwtUserAccessTokenService.verifyAccessToken(request);

            if (currentUserPrincipal != null) {

                if (!currentUserPrincipal.isAccountNonLocked() || !currentUserPrincipal.isEnabled()) {
                    throw new DisabledException("This account is not enabled");
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(currentUserPrincipal, null, currentUserPrincipal.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("JwtAuthenticationFilter → Access token validation failed. Path: {}, Error: {}",
                    request.getRequestURI(),
                    ex.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }


    }
}
