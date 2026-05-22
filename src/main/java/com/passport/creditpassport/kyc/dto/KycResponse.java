package com.passport.creditpassport.kyc.dto;

import com.passport.creditpassport.kyc.model.KYC;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record KycResponse(
        String userId,
        boolean verified,
        Instant createdAt
) {
    public static KycResponse from(KYC kyc) {
        return new KycResponse(
                kyc.getUserId(),
                kyc.isVerified(),
                kyc.getCreatedAt()
        );
    }
}
