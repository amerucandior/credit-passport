package com.passport.creditpassport.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class OtpRequest {

    public record VerifyOtpRequest (
        @Schema(description = "Email address that received the OTP", example = "johndoe@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "Six-digit OTP code from email", example = "824436")
        @NotBlank
        @Pattern(regexp = "\\d{6}", message = "OTP must be exactly 6 digits")
        String otp
        ) {}

    /**
     * Used by the resend-OTP endpoint — only an email address is needed.
     */

    @Builder
    public record EmailOnlyRequest (

        @Schema(description = "Email address for the unverified account", example = "johndoe@example.com")
        @NotBlank
        @Email
        String email
    ) {}
}
