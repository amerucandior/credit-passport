package com.passport.creditpassport.UserProfile.serviceImplementation;

import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;
import com.passport.creditpassport.UserProfile.repository.ProfileRepository;
import com.passport.creditpassport.UserProfile.service.ProfileService;
import com.passport.creditpassport.UserProfile.models.UserProfile;
import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public ProfileResponse createProfile(User authenticatedUser, UpdateProfileRequest request) {

        String userId = authenticatedUser.getId();

        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("UserProfile already exists!");
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setGender(request.getGender());
        userProfile.setDateOfBirth(request.getDateOfBirth());
        userProfile.setEmployerName(request.getEmployerName());
        userProfile.setEmploymentStatus(request.getEmploymentStatus());
        userProfile.setOccupation(request.getOccupation());
        userProfile.setMonthlyIncomeKes(request.getMonthlyIncomeKes());
        userProfile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        UserProfile saved = profileRepository.save(userProfile);
        log.info("Profile created for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }

//    Read user profile
    @Transactional(readOnly = true)
    @Override
    public ProfileResponse getProfile(User authenticatedUser) {
        String userId = authenticatedUser.getId();

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return ProfileResponse.fromEntity(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(User authenticatedUser, UpdateProfileRequest request) {
        String userId = authenticatedUser.getId();

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found — create one first."));

        // Only overwrite fields that were actually supplied
        if (request.getGender()           != null) profile.setGender(request.getGender());
        if (request.getDateOfBirth()      != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getEmployerName()     != null) profile.setEmployerName(request.getEmployerName());
        if (request.getEmploymentStatus() != null) profile.setEmploymentStatus(request.getEmploymentStatus());
        if (request.getOccupation()       != null) profile.setOccupation(request.getOccupation());
        if (request.getMonthlyIncomeKes() != null) profile.setMonthlyIncomeKes(request.getMonthlyIncomeKes());
        if (request.getProfilePhotoUrl()  != null) profile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        UserProfile saved = profileRepository.save(profile);
        log.info("Profile updated for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }
}
