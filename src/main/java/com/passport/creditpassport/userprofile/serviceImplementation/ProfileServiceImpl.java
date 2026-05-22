package com.passport.creditpassport.userprofile.serviceImplementation;

import com.passport.creditpassport.userprofile.dto.ProfileResponse;
import com.passport.creditpassport.userprofile.dto.UpdateProfileRequest;
import com.passport.creditpassport.userprofile.repository.ProfileRepository;
import com.passport.creditpassport.userprofile.service.ProfileService;
import com.passport.creditpassport.userprofile.models.UserProfile;
import com.passport.creditpassport.exception.InvalidProfilePhotoUrlException;
import com.passport.creditpassport.exception.ProfileAlreadyExistsException;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import org.apache.commons.text.StringEscapeUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public ProfileResponse createProfile(String userId, UpdateProfileRequest request) {

        if (profileRepository.existsByUserId(userId)) {
            throw new ProfileAlreadyExistsException(userId);
        }

        UserProfile userProfile = getUserProfile(request, userId);

        UserProfile saved = profileRepository.save(userProfile);
        log.info("Profile created for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }

    private static @NonNull UserProfile getUserProfile(UpdateProfileRequest request, String userId) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setGender(request.gender());
        userProfile.setDateOfBirth(request.dateOfBirth());
        userProfile.setEmployerName(request.employerName());
        userProfile.setOccupation(request.occupation());
        userProfile.setSaccoName(request.saccoName());
        userProfile.setMonthlyIncomeKes(request.monthlyIncomeKes());
        userProfile.setProfilePhotoUrl(request.profilePhotoUrl());
        return userProfile;
    }

    //    Read user profile
    @Transactional(readOnly = true)
    @Override
    public ProfileResponse getProfile(String userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return ProfileResponse.fromEntity(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found — create one first."));

        // Only overwrite fields that were actually supplied
        if (request.gender()           != null) profile.setGender(request.gender());
        if (request.dateOfBirth()      != null) profile.setDateOfBirth(request.dateOfBirth());
        if (request.employerName()    != null) profile.setEmployerName(request.employerName());
        if (request.occupation()      != null) profile.setOccupation(request.occupation());
        if (request.saccoName()       != null) profile.setSaccoName(request.saccoName());
        if (request.employmentStatus() != null) profile.setEmploymentStatus(request.employmentStatus());
        if (request.monthlyIncomeKes() != null) profile.setMonthlyIncomeKes(request.monthlyIncomeKes());
        if (request.profilePhotoUrl() != null) profile.setProfilePhotoUrl(request.profilePhotoUrl());
        UserProfile saved = profileRepository.save(profile);
        log.info("Profile updated for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }
}
