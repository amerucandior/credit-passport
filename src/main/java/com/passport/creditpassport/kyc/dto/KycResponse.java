package com.passport.creditpassport.kyc.dto;

import com.passport.creditpassport.kyc.model.KYC;


import java.time.Instant;

public record KycResponse(
        String userId,
        boolean verified,
        Instant createdAt,
        String selfiePicture,
        String nationalIdFront,
        String nationalIdBack,
        String kraPin,
        String latestPayslip
) {
    public static KycResponse from(KYC kyc) {
        return new KycResponse(
                kyc.getUserId(),
                kyc.isVerified(),
                kyc.getCreatedAt(),
                kyc.getSelfiePicture(),
                kyc.getNationalIdFront(),
                kyc.getNationalIdBack(),
                kyc.getKraPin(),
                kyc.getLatestPayslip()
        );
    }
}
