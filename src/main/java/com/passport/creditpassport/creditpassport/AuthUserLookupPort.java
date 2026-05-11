package com.passport.creditpassport.creditpassport;

public interface AuthUserLookupPort {
    boolean existsByNationalId(String nationalId);
}
