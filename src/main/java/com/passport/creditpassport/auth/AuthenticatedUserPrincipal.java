package com.passport.creditpassport.auth;

public record AuthenticatedUserPrincipal(
        String id,
        String natId,
        String name,
        String email,
        String number
) {}
