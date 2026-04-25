package com.passport.creditpassport.auth.serviceimplementation;


import com.passport.creditpassport.auth.Dto.AuthResponse;
import com.passport.creditpassport.auth.Dto.LoginRequest;
import com.passport.creditpassport.auth.Dto.RegisterRequest;
import com.passport.creditpassport.auth.models.user;
import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.auth.service.AuthService;
import com.passport.creditpassport.config.JwtService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        user users = new user();
        users.setName(request.getUserName());
        users.setNumber(request.getUserNumber());
        users.setPassword(passwordEncoder.encode(request.getUserPassword()));
        users.setNatId(request.getUserNationalId());

        usersRepository.save(users);

        String token = JwtService.generateToken(request.getUserNationalId()); // Logic Bug need to use national ID for the token
        return AuthResponse.builder().token(token).build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        users = usersRepository.findByUserName(request.getUserName());


        if (passwordEncoder.matches(request.getUserPassword(), users.getPassword())){
            String token = JwtService.generateToken(request.getUserName();
            return AuthResponse.builder().token(token).build();
        } else {
            throw new RuntimeException("login details incorrect");
        }

        return null;
    }
}
