package com.passport.creditpassport.auth.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class OtpRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyOtpRequest {
        @Schema(description = "Email address that received the OTP", example = "ianmwirigi@outlook.com")
        @NotBlank
        @Email
        private String email;

        @Schema(description = "Six-digit OTP code from email", example = "824436")
        @NotBlank
        @Pattern(regexp = "\\d{6}", message = "OTP must be exactly 6 digits")
        private String otp;
    }

    /**
     * Used by the resend-OTP endpoint — only an email address is needed.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmailOnlyRequest {

        @Schema(description = "Email address for the unverified account", example = "ianmwirigi@outlook.com")
        @NotBlank
        @Email
        private String email;
    }

}
