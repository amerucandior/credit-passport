package com.passport.creditpassport.auth.serviceimplementation;


import com.passport.creditpassport.auth.Dto.AuthResponse;
import com.passport.creditpassport.auth.Dto.LoginRequest;
import com.passport.creditpassport.auth.Dto.RegisterRequest;
import com.passport.creditpassport.auth.models.user;
import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.auth.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        user users = new user();
        users.setName(request.getUserName());
        users.setNumber(request.getUserNumber());
        users.setPassword(passwordEncoder.encode(request.getUserPassword()));
        users.setNatId(request.getUserNationalId());

        usersRepository.save(users);
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        users = usersRepository.findByUserName(request.getUserName());


        if (passwordEncoder.matches(request.getUserPassword(), users.getPassword())){
            // pass jwt token
            return null;
        } else {
            throw new RuntimeException("login details incorrect");
        }

        return null;
    }
}
