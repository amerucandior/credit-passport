package com.passport.creditpassport.lender.lendermodel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "lenders", uniqueConstraints = {
        @UniqueConstraint(columnNames = "lender_id"),
        @UniqueConstraint(columnNames = "lender_name"),
        @UniqueConstraint(columnNames = "cbk_license_no"),
        @UniqueConstraint(columnNames = "certificate_of_incorporation"),
        @UniqueConstraint(columnNames = "memorandum_articles_of_association"),
        @UniqueConstraint(columnNames = "reg_business_address")
        })
public class Lender {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "lender_id")
    private String lenderId;

    @Column(name = "lender_name") // todo: guard against xss
    private String lenderName;

    @Column(name = "cbk_license_no")
    private String cbkLicenseNo;

    @Column(name = "certificate_of_incorporation")
    private String certificateOfIncorporation;

    @Column(name = "memorandum_articles_of_association", nullable = true)
    private String memorandumArticlesOfAssociation;

    @Column(name = "reg_business_address")
    private String regBusinessAddress;

    @Column(name = "name_approval_proof", nullable = true)
    private String nameApprovalProof;

    @Column(name = "api_key", unique = true, nullable = false)
    private String apiKey;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean enabled = false;    // becomes true after lender meets requirements
    // used in future to soft block lenders from accessing credit-passports
}
