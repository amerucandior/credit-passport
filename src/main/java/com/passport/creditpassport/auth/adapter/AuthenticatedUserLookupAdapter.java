package com.passport.creditpassport.auth.adapter;

import com.passport.creditpassport.auth.AuthenticatedUserLookupPort;
import com.passport.creditpassport.auth.AuthenticatedUserPrincipal;
import com.passport.creditpassport.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserLookupAdapter implements AuthenticatedUserLookupPort {

    private final UsersRepository usersRepository;

    @Override
    public Optional<AuthenticatedUserPrincipal> findById(String userId) {
        return usersRepository.findById(userId)
                .map(user -> new AuthenticatedUserPrincipal(user.getId()));
    }
}
