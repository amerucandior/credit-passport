package com.passport.creditpassport.creditpassport.dto;

import com.passport.creditpassport.creditpassport.model.CreditPassport;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Credit passport issued to a verified borrower")
public record CreditPassportResponse(

        @Schema(description = "Unique passport identifier")
        UUID passportId,

        @Schema(description = "National ID number")
        String nationalId,

        @Schema(description = "Full name of the passport holder")
        String name,

        @Schema(description = "Email address of the passport holder")
        String email,

        @Schema(description = "Phone number of the passport holder")
        String phoneNumber,

        @Schema(description = "Date of birth", format = "date")
        LocalDate dateOfBirth,

        @Schema(description = "Computed credit score (300–1000)")
        Integer creditScore,

        @Schema(description = "Credit score band", example = "GOOD",
                allowableValues = {"EXCELLENT", "GOOD", "FAIR", "POOR"})
        String creditScoreBand,

        @Schema(description = "Timestamp when this passport was issued")
        Instant issuedAt,

        @Schema(description = "Timestamp when this passport expires (issuedAt + 90 days)")
        Instant expiresAt,

        @Schema(description = "Record creation timestamp")
        Instant createdAt,

        @Schema(description = "Record last-updated timestamp")
        Instant updatedAt
) {
    /**
     * Maps a {@link CreditPassport} JPA entity to this response DTO.
     * The {@code User} association on the entity is never touched here,
     * so Springdoc will not introspect it, and the schema stays clean.
     */
    public static CreditPassportResponse fromEntity(CreditPassport passport) {
        return new CreditPassportResponse(
                passport.getPassportId(),
                passport.getNationalId(),
                passport.getName(),
                passport.getEmail(),
                passport.getPhoneNumber(),
                passport.getDateOfBirth(),
                passport.getCreditScore(),
                passport.getCreditScoreBand(),
                passport.getIssuedAt(),
                passport.getExpiresAt(),
                passport.getCreatedAt(),
                passport.getUpdatedAt()
        );
    }
}