package com.passport.creditpassport.lender.lenderdto;

import com.passport.creditpassport.creditpassport.model.CreditPassport;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Lender credit passport view.
 * Intentionally narrower
 */
@Schema(description = "Credit passport data returned to an authorised lender")
public record LenderCreditPassportResponse(

        @Schema(description = "National ID number of the borrower")
        String nationalId,

        @Schema(description = "Full name of the borrower")
        String name,

        @Schema(description = "Computed credit score (300–1000)")
        Integer creditScore,

        @Schema(description = "Credit score band", example = "GOOD",
                allowableValues = {"EXCELLENT", "GOOD", "FAIR", "POOR"})
        String creditScoreBand,

        @Schema(description = "Timestamp when this passport was issued")
        Instant issuedAt,

        @Schema(description = "Timestamp when this passport expires")
        Instant expiresAt
) {
    public static LenderCreditPassportResponse fromEntity(CreditPassport passport) {
        return new LenderCreditPassportResponse(
                passport.getNationalId(),
                passport.getName(),
                passport.getCreditScore(),
                passport.getCreditScoreBand(),
                passport.getIssuedAt(),
                passport.getExpiresAt()
        );
    }
}