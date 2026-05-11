package com.passport.creditpassport.UserProfile.dto;

import com.passport.creditpassport.UserProfile.models.EmploymentStatus;
import com.passport.creditpassport.UserProfile.models.Gender;
import com.passport.creditpassport.UserProfile.models.UserProfile;
import lombok.Builder;
import lombok.Data;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;



@Data
@Builder
public class ProfileResponse {
    private String profileId;
    private String userId;
    private LocalDate dateOfBirth;
    private Gender gender;
    private EmploymentStatus employmentStatus;
    private String employerName;
    private String occupation;
    private String saccoName;
    private BigDecimal monthlyIncomeKes;
    private String profilePhotoUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public static ProfileResponse fromEntity(UserProfile profile) {
        return ProfileResponse.builder()
                .profileId(profile.getProfileId())
                .userId(profile.getUserId())
                .gender(profile.getGender())
                .employmentStatus(profile.getEmploymentStatus())
                .employerName(profile.getEmployerName())
                .occupation(profile.getOccupation())
                .saccoName(profile.getSaccoName())
                .dateOfBirth(profile.getDateOfBirth())
                .monthlyIncomeKes(profile.getMonthlyIncomeKes())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}

