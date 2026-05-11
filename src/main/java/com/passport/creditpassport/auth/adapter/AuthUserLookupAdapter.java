package com.passport.creditpassport.auth.adapter;

import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.creditpassport.AuthUserLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserLookupAdapter implements AuthUserLookupPort {

    private final UsersRepository usersRepository;

    @Override
    public boolean existsByNationalId(String nationalId) {
        return usersRepository.existsByNatId(nationalId);
    }
}
