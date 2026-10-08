package com.passport.creditpassport.userprofile.dto;

import com.passport.creditpassport.userprofile.models.EmploymentStatus;
import com.passport.creditpassport.userprofile.models.Gender;
import com.passport.creditpassport.userprofile.models.UserProfile;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileResponseTest {

    @Test
    void fromEntity_shouldMapAllFieldsCorrectly_andPreserveEmailAndNumberOrder() {
        UserProfile profile = new UserProfile();
        profile.setProfileId("profile-123");
        profile.setUserId("user-456");
        profile.setDateOfBirth(LocalDate.of(1995, 5, 20));
        profile.setGender(Gender.MALE);
        profile.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        profile.setEmployerName("Acme Corp");
        profile.setOccupation("Software Engineer");
        profile.setSaccoName("Stima Sacco");
        profile.setMonthlyIncomeKes(new BigDecimal("150000.00"));
        profile.setProfilePhotoUrl("https://cloudinary.com/photo.jpg");
        Instant now = Instant.now();
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);

        String name = "John Doe";
        String email = "john.doe@example.com";
        String number = "+254712345678";
        String natId = "12345678";

        ProfileResponse response = ProfileResponse.fromEntity(profile, name, email, number, natId);

        assertThat(response.profileId()).isEqualTo("profile-123");
        assertThat(response.userId()).isEqualTo("user-456");
        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.number()).isEqualTo("+254712345678");
        assertThat(response.natId()).isEqualTo("12345678");
        assertThat(response.dateOfBirth()).isEqualTo(LocalDate.of(1995, 5, 20));
        assertThat(response.gender()).isEqualTo(Gender.MALE);
        assertThat(response.employmentStatus()).isEqualTo(EmploymentStatus.EMPLOYED);
        assertThat(response.employerName()).isEqualTo("Acme Corp");
        assertThat(response.occupation()).isEqualTo("Software Engineer");
        assertThat(response.saccoName()).isEqualTo("Stima Sacco");
        assertThat(response.monthlyIncomeKes()).isEqualByComparingTo(new BigDecimal("150000.00"));
        assertThat(response.profilePhotoUrl()).isEqualTo("https://cloudinary.com/photo.jpg");
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);
    }

    @Test
    void builder_shouldCreateProfileResponse() {
        ProfileResponse response = ProfileResponse.builder()
                .profileId("profile-1")
                .userId("user-1")
                .name("Jane Doe")
                .email("jane@example.com")
                .number("+254700000000")
                .natId("87654321")
                .build();

        assertThat(response.profileId()).isEqualTo("profile-1");
        assertThat(response.userId()).isEqualTo("user-1");
        assertThat(response.name()).isEqualTo("Jane Doe");
        assertThat(response.email()).isEqualTo("jane@example.com");
        assertThat(response.number()).isEqualTo("+254700000000");
        assertThat(response.natId()).isEqualTo("87654321");
    }
}
