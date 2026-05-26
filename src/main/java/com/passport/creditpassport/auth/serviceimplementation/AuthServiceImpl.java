package com.passport.creditpassport.auth.serviceimplementation;

import com.passport.creditpassport.auth.dto.LoginRequest;
import com.passport.creditpassport.auth.dto.RegisterRequest;
import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.auth.UsersRepository;
import com.passport.creditpassport.auth.service.AuthService;
import com.passport.creditpassport.exception.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;

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

        if (usersRepository.existsByNatId(request.userNationalId())) {
            throw new NationalIdAlreadyExistsException(request.userNationalId());
        }
        if (usersRepository.existsByNumber(request.number())) {
            throw new NumberAlreadyExistsException(request.number());
        }
        if (usersRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User newUser = new User();
        newUser.setName(request.name());
        newUser.setNumber(request.number());
        newUser.setPassword(passwordEncoder.encode(request.userPassword()));
        newUser.setNatId(request.userNationalId());
        newUser.setEmail(request.email());

        try {
            usersRepository.save(newUser);
            log.info("User '{}' <{}> created - awaiting OTP verification", request.name(), request.email());
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate value detected during registration: {}", e.getMessage());
            String msg = e.getMostSpecificCause().getMessage().toLowerCase();
            throw switch (msg) {
                case String m when m.contains("user_name") -> new NameAlreadyExistsException(request.name());
                case String m when m.contains("national_id") -> new NationalIdAlreadyExistsException(request.userNationalId());
                case String m when m.contains("user_no") -> new NumberAlreadyExistsException(request.number());
                case String m when m.contains("email") -> new EmailAlreadyExistsException(request.email());
                default -> new DuplicateAuthExceptions("User already exists");
            };
        }
    }

    /**
     * Validates email and password only. No JWT is issued here.
     * Delegates to authenticate() so the logic lives in one place.
     */
    @Override
    public void initiateLogin(LoginRequest request) {
        authenticate(new UsernamePasswordAuthenticationToken(request.identifier(), request.userPassword()));
    }

    /**
     * Spring Security AuthenticationManager contract.
     * Called internally by initiateLogin() and by LoginOtpImpl directly.
     */
    @Override
    public @NonNull Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        User found = identifier.contains("@")
                ? usersRepository.findByEmail(identifier)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"))
                : usersRepository.findByNatId(identifier)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, found.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + found.getUserType().name())
        );
        return new UsernamePasswordAuthenticationToken(found, null, authorities);
    }
}