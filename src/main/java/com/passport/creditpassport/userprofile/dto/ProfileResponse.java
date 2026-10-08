package com.passport.creditpassport.userprofile.dto;

import com.passport.creditpassport.userprofile.models.EmploymentStatus;
import com.passport.creditpassport.userprofile.models.Gender;
import com.passport.creditpassport.userprofile.models.UserProfile;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Builder
public record ProfileResponse(
        String profileId,
        String userId,
        String name,
        String email,
        String number,
        String natId,
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

    public static ProfileResponse fromEntity(UserProfile profile, String name, String email, String number, String natId) {
        return ProfileResponse.builder()
                .profileId(profile.getProfileId())
                .userId(profile.getUserId())
                .name(name)
                .email(email)
                .number(number)
                .natId(natId)
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .employmentStatus(profile.getEmploymentStatus())
                .employerName(profile.getEmployerName())
                .occupation(profile.getOccupation())
                .saccoName(profile.getSaccoName())
                .monthlyIncomeKes(profile.getMonthlyIncomeKes())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}

