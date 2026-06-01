package com.passport.creditpassport.auth.service;

import com.passport.creditpassport.auth.dto.RegisterRequest;
import com.passport.creditpassport.auth.dto.LoginRequest;

/**
 * Core auth contract.
 * register() → void: saves the user only; OTP is sent by RegistrationOtpImpl
 * initiateLogin() → void: validates credentials and sends a login OTP
 * helper logic lives in OtpService (its own class).
 * JWT issuance lives exclusively in LoginOtpImpl.verifyLoginOtp().
 */
public interface AuthService {

    /**
     * Creates the account. Does NOT issue a JWT.
     * The caller must invoke RegistrationOtpImpl.sendRegistrationOtp(email) next.
     */
    void register(RegisterRequest request);

    /**
     * Validates credentials. Does NOT issue a JWT.
     * The caller must invoke LoginOtpImpl.initiateLogin(email, password) next.
     */
    void initiateLogin(LoginRequest request);

    void sendLoginEmailVerificationOtp(String email, String otpCode);
}