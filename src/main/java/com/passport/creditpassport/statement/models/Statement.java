package com.passport.creditpassport.statement.models;

import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.statement.enums.StatementType;
import jakarta.persistence.*;
import lombok.*;

import com.github.f4b6a3.uuid.UuidCreator;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Builder
@Setter
@Getter
@Entity
@Table( name = "statements")
@NoArgsConstructor
@AllArgsConstructor
public class Statement {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "user_id", nullable = false, updatable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "statement_type", nullable = false)
    private StatementType statementType;

    @Column(name = "institution_hint")
    private String institutionHint;

    // extracted from the uploaded file on upload
    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "cloudinary_public_id")
    private String cloudinaryPublicId;

    @Column(name = "statement_url")
    private String statementUrl;

    @Column(name = "account_alias")
    private String accountAlias;

    @Column(name = "statement_password")
    private String statementPassword;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean parsed = false; // becomes true after parsing is completed
}
