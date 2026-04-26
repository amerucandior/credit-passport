package com.passport.creditpassport.auth.serviceimplementation;


import com.passport.creditpassport.auth.Dto.AuthResponse;
import com.passport.creditpassport.auth.Dto.LoginRequest;
import com.passport.creditpassport.auth.Dto.RegisterRequest;
import com.passport.creditpassport.exception.DuplicateAuthExceptions;
import com.passport.creditpassport.exception.NameAlreadyExistsException;
import com.passport.creditpassport.exception.NationalIdAlreadyExistsException;
import com.passport.creditpassport.exception.NumberAlreadyExistsException;
import com.passport.creditpassport.auth.models.user;
import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.auth.service.AuthService;
import com.passport.creditpassport.config.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

    //  Pre Check for duplicates
        if (usersRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException(request.getName());
        }
        if (usersRepository.existsByNatId(request.getUserNationalId())) {
            throw new NationalIdAlreadyExistsException(request.getUserNationalId());
        }
        if (usersRepository.existsByNumber(String.valueOf(request.getNumber()))) {
            throw new NumberAlreadyExistsException(request.getNumber());
        }


            user users = new user();
            users.setName(request.getName());
            users.setNumber(request.getNumber());
            users.setPassword(passwordEncoder.encode(request.getUserPassword()));
            users.setNatId(request.getUserNationalId());

        try {
            user savedUser = usersRepository.save(users);
            String token = JwtService.generateToken(savedUser.getNatId());
            log.info("User {} registered successfully", request.getName());
            return AuthResponse.builder().token(token).build();
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate value detected during user registration: {}", e.getMessage());

            // Determine which column caused the Duplication Error
            String msg = e.getMostSpecificCause().getMessage().toLowerCase();
            if (msg.contains("user_name")) {
                throw  new NameAlreadyExistsException(request.getName());
            } else if (msg.contains("national_id")) {
                throw  new NationalIdAlreadyExistsException(request.getUserNationalId());
            } else if (msg.contains("user_no")) {
                throw  new NumberAlreadyExistsException(request.getNumber());
            } else {
                throw new DuplicateAuthExceptions("user already exists");
            }
        }
    }

    @Transactional
    @Override
    public AuthResponse login(LoginRequest request) {
        user users = usersRepository.findByNatId(request.getUserNationalId()).orElseThrow(() -> new RuntimeException("user not found"));

        if (passwordEncoder.matches(request.getUserPassword(), users.getPassword())){
            String token = JwtService.generateToken(users.getNatId());
            return AuthResponse.builder().token(token).build();
        } else {
            throw new RuntimeException("login details incorrect");
        }
    }
}
