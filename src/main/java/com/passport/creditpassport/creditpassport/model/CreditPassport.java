package com.passport.creditpassport.creditpassport.model;

import com.github.f4b6a3.uuid.UuidCreator;
import com.passport.creditpassport.auth.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "credit_passports")
public class CreditPassport {

    @Id
    @Column(name = "passport_id")
    @Builder.Default
    private UUID passportId = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_creditpassport_user")
    )
    private User user;

    // Link back to the user by nationalId — no FK join needed across services
    @Column(name = "national_id", unique = true, nullable = false)
    private String nationalId;

    // Personal details (sourced from User)
    @Column(name = "username", nullable = false)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Credit score (computed by fastAPI microservice)
    @Column(name = "credit_score")
    private Integer creditScore;           // 300 – 1000

    @Column(name = "credit_score_band")
    private String creditScoreBand;        // "GOOD", "FAIR", "POOR"

    // Passport validity
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;             // issuedAt + 90 days

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
