package com.passport.creditpassport.creditpassport;
//Credit-passport model
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "credit_passports")
public class CreditPassport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "passport_id")
    private String passportId;

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

    // Repayment behaviour // I also think these should be user supplied?
    @Column(name = "total_loans_taken")
    private Integer totalLoansTaken;

    @Column(name = "loans_repaid_on_time")
    private Integer loansRepaidOnTime;

    @Column(name = "loans_defaulted")
    private Integer loansDefaulted;

    @Column(name = "repayment_rate")
    private Double repaymentRate;          // 0.95 = 95%

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
