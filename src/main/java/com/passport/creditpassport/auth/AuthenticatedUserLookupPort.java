package com.passport.creditpassport.auth;

import java.util.Optional;

public interface AuthenticatedUserLookupPort {
    Optional<AuthenticatedUserPrincipal> findById(String userId);
}
