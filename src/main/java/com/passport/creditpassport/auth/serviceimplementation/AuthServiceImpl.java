package com.passport.creditpassport.auth.serviceimplementation;

import com.passport.creditpassport.auth.Dto.LoginRequest;
import com.passport.creditpassport.auth.Dto.RegisterRequest;
import com.passport.creditpassport.auth.models.user;
import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.auth.service.AuthService;
import com.passport.creditpassport.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService, AuthenticationManager {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Saves the new account. No JWT is issued — the account is disabled until
     * the registration OTP is verified by RegistrationOtpImpl.
     */
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (usersRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException(request.getName());
        }
        if (usersRepository.existsByNatId(request.getUserNationalId())) {
            throw new NationalIdAlreadyExistsException(request.getUserNationalId());
        }
        if (usersRepository.existsByNumber(request.getNumber())) {
            throw new NumberAlreadyExistsException(request.getNumber());
        }
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        user newUser = new user();
        newUser.setName(request.getName());
        newUser.setNumber(request.getNumber());
        newUser.setPassword(passwordEncoder.encode(request.getUserPassword()));
        newUser.setNatId(request.getUserNationalId());
        newUser.setEmail(request.getEmail());

        try {
            usersRepository.save(newUser);
            log.info("User '{}' <{}> created — awaiting OTP verification", request.getName(), request.getEmail());
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate value detected during registration: {}", e.getMessage());
            String msg = e.getMostSpecificCause().getMessage().toLowerCase();
            if (msg.contains("user_name"))        throw new NameAlreadyExistsException(request.getName());
            else if (msg.contains("national_id")) throw new NationalIdAlreadyExistsException(request.getUserNationalId());
            else if (msg.contains("user_no"))     throw new NumberAlreadyExistsException(request.getNumber());
            else if (msg.contains("email"))       throw new EmailAlreadyExistsException(request.getEmail());
            else                                  throw new DuplicateAuthExceptions("User already exists");
        }
    }

    /**
     * Validates email + password only. No JWT is issued here.
     * Delegates to authenticate() so the logic lives in one place.
     */
    @Override
    public void initiateLogin(LoginRequest request) {
        authenticate(new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getUserPassword()));
    }

    /**
     * Spring Security AuthenticationManager contract.
     * Called internally by initiateLogin() and by LoginOtpImpl directly.
     */
    @Override
    public @NonNull Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        user found = usersRepository.findByEmailOrNatId(identifier, identifier)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, found.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(found, null, Collections.emptyList());
    }
}