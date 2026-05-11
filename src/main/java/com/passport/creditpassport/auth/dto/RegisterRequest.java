package com.passport.creditpassport.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record RegisterRequest (

    @Schema(description = "Full name or username", example = "John Doe")
    @NotBlank(message = "Enter a valid username")
    @Size(min = 3, max = 50)
    String name,

    @Schema(description = "Phone number", example = "0712345678")
    @NotBlank(message = "Enter a valid phone number")
    String number,

    @Schema(description = "Password with at least 8 characters", example = "123@#$df")
    @NotBlank(message = "Enter a password")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    String userPassword,

    @Schema(description = "Kenyan national ID number", example = "33334444")
    @NotBlank(message = "Enter your national ID")
    @Pattern(regexp = "\\d{7,8}", message = "Invalid Kenyan ID")
    String userNationalId,

    @Schema(description = "Email address used for OTP verification", example = "creditpassport@example.com")
    @NotBlank(message = "Enter your email address")
    @Email(message = "Enter a valid email address")
    String email
    ) {}
