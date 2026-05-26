package com.passport.creditpassport.auth.models;

import com.passport.creditpassport.auth.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id"),
        @UniqueConstraint(columnNames = "national_id"),
        @UniqueConstraint(columnNames = "user_no"),
        @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType = UserType.BORROWER;

    @Column(name = "national_id", unique = true, nullable = false)
    private String natId;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_no", unique = true, nullable = false)
    private String number;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean enabled = false;      // becomes true after email OTP verification

    // Registration OTP
    @Column(name = "registration_otp_hash")
    private String registrationOtp;

    @Column(name = "registration_otp_expires_at")
    private Instant registrationOtpExpiresAt;

    // Login OTP
    @Column(name = "login_otp_hash")
    private String loginOtp;

    @Column(name = "login_otp_expires_at")
    private Instant loginOtpExpiresAt;

    // OTP attempts
    @Column(nullable = false)
    private int loginOtpAttempts = 0;

    // Auditing
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
