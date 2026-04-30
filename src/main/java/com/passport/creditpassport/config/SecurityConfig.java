package com.passport.creditpassport.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity          // enables @PreAuthorize / @Secured on service methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {        // camelCase — Spring looks up beans by name
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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

                        // Public — no token required
                        .requestMatchers(
                                "/api/auth/**",         // register, login, verify-*, resend-*
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
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}