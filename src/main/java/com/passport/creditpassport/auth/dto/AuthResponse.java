package com.passport.creditpassport.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse(String token) {}
