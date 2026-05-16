package com.passport.creditpassport.userprofile.models;

import com.passport.creditpassport.auth.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "user_profile", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id")
    private String profileId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_user_profile")
    )
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    @Column(name = "employer_name")
    private String employerName;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "monthly_income_kes", precision = 15, scale = 2)
    private BigDecimal monthlyIncomeKes;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name="sacco_name")
    private String saccoName;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
