package com.passport.creditpassport.config;

import com.passport.creditpassport.auth.AuthenticatedUserLookupPort;
import com.passport.creditpassport.auth.JwtTokenPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Collections;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenPort jwtTokenPort;
    private final AuthenticatedUserLookupPort authenticatedUserLookupPort;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Read the Authorization header.
        //    If absent or not a Bearer token — public endpoint or missing credentials.
        //    Step aside; ExceptionTranslationFilter produces the 401.
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Slice off "Bearer " (7 chars) to get the raw token string.
        final String token = authHeader.substring(7);

        // 3. Validate and extract the subject (UUID stored when the token was issued).
        //    Any exception means the token is unusable — log quietly and skip.
        final String userId;

        try {
            if (!jwtTokenPort.validateToken(token)) {
                log.debug("JWT validation failed on path {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            userId = jwtTokenPort.extractSubject(token);
        } catch (Exception e) {
            log.debug("Could not process JWT: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Skip if no subject, or if this thread already carries authentication.
        if (userId == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Confirm the user still exists.
        //    Subject = entity UUID primary key → findById is unambiguous.
        //    Deactivated/deleted accounts are rejected here regardless of token expiry.
        var authenticatedUser = authenticatedUserLookupPort.findById(userId);
        if (authenticatedUser.isEmpty()) {
            log.debug("JWT subject '{}' not found in database", userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 6. Build an authenticated token.
        //    Three-arg constructor → isAuthenticated() == true.
        //    credentials = null — password not needed after JWT validation.
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        authenticatedUser.get(),
                        null,
                        Collections.emptyList()
                );

        // 7. Attach IP and session metadata for audit logging.
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 8. Publish identity to the current thread.
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Authenticated user '{}' on path {}", userId, request.getRequestURI());

        // 9. Always continue — this filter identifies, it never gates.
        filterChain.doFilter(request, response);
    }
}