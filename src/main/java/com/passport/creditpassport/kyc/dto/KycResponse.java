package com.passport.creditpassport.kyc.dto;

import com.passport.creditpassport.kyc.model.KYC;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record KycResponse(

        String createdAt,
        boolean verified,
        Instant userId
) {
    public static KycResponse from(KYC response) {
        return new KycResponse(
                response.getUserId(),
                response.isVerified(),
                response.getCreatedAt()
        );
    }
}
