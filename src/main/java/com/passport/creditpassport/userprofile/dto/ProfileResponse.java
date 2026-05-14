package com.passport.creditpassport.userprofile.dto;

import com.passport.creditpassport.userprofile.models.EmploymentStatus;
import com.passport.creditpassport.userprofile.models.Gender;
import com.passport.creditpassport.userprofile.models.UserProfile;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;


public record ProfileResponse(
        String profileId,
        String userId,
        LocalDate dateOfBirth,
        Gender gender,
        EmploymentStatus employmentStatus,
        String employerName,
        String occupation,
        String saccoName,
        BigDecimal monthlyIncomeKes,
        String profilePhotoUrl,
        Instant createdAt,
        Instant updatedAt) {

    public static ProfileResponse fromEntity(UserProfile profile) {
        return new ProfileResponse(
                profile.getProfileId(),
                profile.getUserId(),
                profile.getDateOfBirth(),
                profile.getGender(),
                profile.getEmploymentStatus(),
                profile.getEmployerName(),
                profile.getOccupation(),
                profile.getSaccoName(),
                profile.getMonthlyIncomeKes(),
                profile.getProfilePhotoUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}

