package com.coursehub.commons.security.filter;

import com.coursehub.commons.security.service.JwtInternalTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

public class InternalCallFilter extends OncePerRequestFilter {

    private final JwtInternalTokenService jwtInternalTokenService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private static final Logger  log = LoggerFactory.getLogger(InternalCallFilter.class);

    public InternalCallFilter(JwtInternalTokenService jwtInternalTokenService,
                              HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtInternalTokenService = jwtInternalTokenService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {

        try {

            String path = request.getRequestURI();

            if (!path.contains("/internal/")) {
                filterChain.doFilter(request, response);
                return;
            }

            boolean isValidInternalToken = jwtInternalTokenService.verifyInternalToken(request);

            if (!isValidInternalToken) {
                throw new JwtException("INVALID_INTERNAL_TOKEN");
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("InternalCallFilter → Internal token validation failed. Path: {}, Error: {}",
                    request.getRequestURI(),
                    ex.getMessage());

            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
