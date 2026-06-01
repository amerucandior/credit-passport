package com.passport.creditpassport.lender.security;

import com.passport.creditpassport.lender.lendermodel.Lender;
import com.passport.creditpassport.lender.repository.LenderRepository;
import com.passport.creditpassport.lender.util.ApiKeyUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final LenderRepository lenderRepository;

    private static final Set<String> SKIP_PREFIXES = Set.of(
            "/api/auth/",
            "/api/lender/register",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    /**
     * OncePerRequestFilter hook — returning true here skips doFilterInternal entirely.
     * This is the correct extension point; avoids duplicating path logic inside the filter body.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean isLenderPath = path != null && path.startsWith("/api/lender/");
        boolean isPublicLenderPath = "/api/lender/register".equals(path);
        // Filter only protected lender routes:
        return !isLenderPath || isPublicLenderPath;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String rawApiKey = request.getHeader("X-API-KEY");

        if (rawApiKey == null || rawApiKey.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing API key\"}");
            return;
        }

        String hashedKey = ApiKeyUtil.hash(rawApiKey);
        Optional<Lender> lender = lenderRepository.findByApiKey(hashedKey);

        if (lender.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid API key or inactive API key\"}");
            return;
        }

        request.setAttribute("authenticatedLender", lender.get());
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_LENDER")
        );

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        lender.get(),
                        null,
                        authorities
                );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}