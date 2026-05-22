package com.passport.creditpassport.kyc.model;

import com.github.f4b6a3.uuid.UuidCreator;
import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.kyc.enums.KycDocType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@Table(name = "kyc")
@NoArgsConstructor
@AllArgsConstructor
public class KYC {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_statements_user")
    )
    private User user;

    @Column(name = "kyc_doc_type")
    @Enumerated(EnumType.STRING)
    private KycDocType kycDocType;

    @Column(name = "selfie_picture")
    private String selfiePicture;

    @Column(name = "national_id_front")
    private String nationalIdFront;

    @Column(name = "national_id_back")
    private String nationalIdBack;

    @Column(name = "kra_pin")
    private String kraPin;

    @Column(name = "latest_payslip")
    private String latestPayslip;

    @Column(name = "cloudinary_public_id")
    private String cloudinaryPublicId;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false; // becomes true after admin verification
}


