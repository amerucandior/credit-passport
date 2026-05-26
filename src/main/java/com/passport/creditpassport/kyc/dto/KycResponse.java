package com.passport.creditpassport.kyc.dto;

import com.passport.creditpassport.kyc.model.KYC;


import java.time.Instant;

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
