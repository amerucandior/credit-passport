package com.passport.creditpassport.userprofile.dto;


import com.passport.creditpassport.userprofile.models.EmploymentStatus;
import com.passport.creditpassport.userprofile.models.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateProfileRequest (

    @Schema(
            description = "Date of birth of the user",
            example = "1995-06-15",
            format = "date"
    )
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    @Schema(
            description = "Gender of the user",
            example = "MALE",
            allowableValues = {"MALE", "FEMALE", "OTHER", "PREFER_NOT_TO_SAY"}
    )
    Gender gender,

    @Schema(
            description = "Current employment status of the user",
            example = "EMPLOYED",
            allowableValues = {"EMPLOYED", "SELF_EMPLOYED", "UNEMPLOYED", "STUDENT", "RETIRED"}
    )
    EmploymentStatus employmentStatus,

    @Schema(
            description = "Name of the user's current employer",
            example = "Safaricom PLC",
            maxLength = 100,
            pattern = "^[^<>\"'&]*$"
    )
    @Pattern(regexp = "^[^<>\"'&]*$", message = "Invalid characters in Employer Name")
    @Size(max = 100, message = "Employer Name must not exceed 100 characters")
    String employerName,


    @Schema(
            description = "Name of the user's current employer",
            example = "Kenya Police Sacco",
            maxLength = 100,
            pattern = "^[^<>\"'&]*$"
    )
    @Pattern(regexp = "^[^<>\"'&]*$", message = "Invalid characters in Sacco Name")
    @Size(max = 100, message = "Sacco Name must not exceed 100 characters")
    String saccoName,

    @Schema(
            description = "User's occupation or job title",
            example = "Software Engineer",
            maxLength = 100,
            pattern = "^[^<>\"'&]*$"
    )
    @Pattern(regexp = "^[^<>\"'&]*$", message = "Invalid characters in Occupation")
    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    String occupation,


    @Schema(
            description = "User's gross monthly income in Kenyan Shillings",
            example = "85000.00",
            minimum = "0.01",
            format = "decimal"
    )
    @DecimalMin(value = "0.00", inclusive = false, message = "Monthly income must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Monthly income must have at most 10 integer digits and 2 decimal places")
    BigDecimal monthlyIncomeKes,


    @Schema(
            description = "Publicly accessible URL of the user's profile photo",
            example = "https://cdn.creditpassport.com/photos/user-abc123.jpg",
            maxLength = 500,
            format = "uri"
    )
    @URL(message = "Profile photo must be a valid URL")
    @Size(max = 500, message = "Profile photo URL must not exceed 500 characters")
    String profilePhotoUrl
    ) {}
