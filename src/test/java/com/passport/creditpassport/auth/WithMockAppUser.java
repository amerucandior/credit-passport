package com.passport.creditpassport.auth;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test-only equivalent of {@link org.springframework.security.test.context.support.WithMockUser}
 * that populates the SecurityContext with an {@link AuthenticatedUserPrincipal} — the principal
 * type that controllers using {@code @AuthenticationPrincipal AuthenticatedUserPrincipal} expect.
 *
 * <p>The stock {@code @WithMockUser} injects a {@code UserDetails} principal, which Spring's
 * {@code @AuthenticationPrincipal} resolver discards as {@code null} when the target parameter
 * type is {@code AuthenticatedUserPrincipal}, leading to NPEs in the controller.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAppUserSecurityContextFactory.class)
public @interface WithMockAppUser {
    String id() default "user-123";
}
