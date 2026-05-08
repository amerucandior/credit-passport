package com.passport.creditpassport.config;

import com.passport.creditpassport.lender.security.ApiKeyAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity          // enables @PreAuthorize / @Secured on service methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    @Value("${app.cors.allowed-origins:http://localhost:8080,http://127.0.0.1:8080,http://16.171.148.5:8080}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {        // camelCase — Spring looks up beans by name
        return new BCryptPasswordEncoder();
    }

    // --- CORS ------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Spring matches origins exactly; remove trailing slash to avoid mismatches.
        config.setAllowedOrigins(
                Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .map(origin -> origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin)
                        .distinct()
                        .toList()
        );
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "X-API-KEY"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // ── CSRF ──────────────────────────────────────────────────────────
                // Disabled because this is a stateless REST API authenticated via JWT.
                // CSRF protection is only meaningful for browser session-cookie flows.
                .csrf(AbstractHttpConfigurer::disable)

                // ── Session management ────────────────────────────────────────────
                // STATELESS: Spring will never create or consult an HttpSession.
                // Every request must carry its own credentials (the JWT).
                // This also prevents Spring from issuing a JSESSIONID cookie.
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Route rules ───────────────────────────────────────────────────
                // Rules are evaluated top-to-bottom; the first match wins.
                .authorizeHttpRequests(auth -> auth
                        // Allow CORS preflight requests through.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public — no token required
                        .requestMatchers(
                                "/api/auth/**",         // register, login, verify-*, resend-*
                                "/api/lender/**",       // api protected
                                "/v3/api-docs/**",      // OpenAPI spec
                                "/swagger-ui/**",       // Swagger UI assets
                                "/swagger-ui.html"      // Swagger UI entry point
                        ).permitAll()

                        // Protected — valid JWT required; unauthenticated requests get 401
                        .requestMatchers("/api/profile/**").authenticated()

                        // Everything else also requires authentication
                        .anyRequest().authenticated()
                )


                // ── JWT filter ────────────────────────────────────────────────────
                // Runs before Spring's own UsernamePasswordAuthenticationFilter.
                // It reads the Bearer token, validates it, and populates the
                // SecurityContext so downstream rules see an authenticated principal.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // --- API KEY FILTER
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // XSS filter
                // prevents a malicious actor from storing malicious code to run on clients device
                .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives("default-src 'self'; script-src 'self'; object-src 'none';"))
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny));

        return http.build();
    }
}