package com.passport.creditpassport.UserProfile.dto;


import com.passport.creditpassport.UserProfile.models.EmploymentStatus;
import com.passport.creditpassport.UserProfile.models.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    private EmploymentStatus employmentStatus;

    @Size(max = 100, message = "Employer Name must not exceed 100 characters")
    private String employerName;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @DecimalMin(value = "0.00", inclusive = false, message = "Monthly income must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Monthly income must have at most 10 integer digits and 2 decimal places")
    private BigDecimal monthlyIncomeKes;

    @URL(message = "Profile photo must be a valid URL")
    @Size(max = 500, message = "Profile photo URL must not exceed 500 characters")
    private String profilePhotoUrl;
}
