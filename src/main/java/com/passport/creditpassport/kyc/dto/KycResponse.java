package com.passport.creditpassport.kyc.dto;

import com.passport.creditpassport.kyc.model.KYC;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "KYC document status for the authenticated user")
public record KycResponse(

        @Schema(description = "Internal user ID")
        String userId,

        @Schema(description = "Whether this KYC submission has been verified by an admin")
        boolean verified,

        @Schema(description = "Timestamp when KYC was first submitted")
        Instant createdAt,

        /**
         * Signed Cloudinary URLs — present in the HTTP response for the
         * authenticated user but hidden from the public Swagger schema to
         * avoid advertising sensitive document URL patterns.
         */
        @Schema(hidden = true)
        String selfiePicture,

        @Schema(hidden = true)
        String nationalIdFront,

        @Schema(hidden = true)
        String nationalIdBack,

        @Schema(hidden = true)
        String kraPin,

        @Schema(hidden = true)
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