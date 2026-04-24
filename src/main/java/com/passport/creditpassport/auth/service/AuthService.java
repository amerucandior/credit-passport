package com.passport.creditpassport.auth.service;

import com.passport.creditpassport.auth.Dto.AuthResponse;
import com.passport.creditpassport.auth.Dto.LoginRequest;
import com.passport.creditpassport.auth.Dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);

}
