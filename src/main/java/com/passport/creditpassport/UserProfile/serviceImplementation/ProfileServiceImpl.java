package com.passport.creditpassport.UserProfile.serviceImplementation;

import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;
import com.passport.creditpassport.UserProfile.repository.ProfileRepository;
import com.passport.creditpassport.UserProfile.service.ProfileService;
import com.passport.creditpassport.UserProfile.models.UserProfile;
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

    private static String sanitizeUrl(String url) {
        if (url == null) return null;
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"https".equalsIgnoreCase(scheme) && !"http".equalsIgnoreCase(scheme)) {
                throw new InvalidProfilePhotoUrlException();
            }
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new InvalidProfilePhotoUrlException();
        }
    }


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
        userProfile.setEmployerName(
                StringEscapeUtils.escapeHtml4(request.employerName()));
        userProfile.setEmploymentStatus(request.employmentStatus());
        userProfile.setOccupation(
                StringEscapeUtils.escapeHtml4(request.occupation()));
        userProfile.setSaccoName(
                StringEscapeUtils.escapeHtml4(request.saccoName()));
        userProfile.setMonthlyIncomeKes(request.monthlyIncomeKes());
        userProfile.setProfilePhotoUrl(
                sanitizeUrl(request.profilePhotoUrl()));
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
        if (request.employerName()    != null) profile.setEmployerName(
                StringEscapeUtils.escapeHtml4(request.employerName()));
        if (request.occupation()      != null) profile.setOccupation(
                StringEscapeUtils.escapeHtml4(request.occupation()));
        if (request.saccoName()       != null) profile.setSaccoName(
                StringEscapeUtils.escapeHtml4(request.saccoName()));
        if (request.employmentStatus() != null) profile.setEmploymentStatus(request.employmentStatus());
        if (request.monthlyIncomeKes() != null) profile.setMonthlyIncomeKes(request.monthlyIncomeKes());
        if (request.profilePhotoUrl() != null) profile.setProfilePhotoUrl(
                sanitizeUrl(request.profilePhotoUrl()));
        UserProfile saved = profileRepository.save(profile);
        log.info("Profile updated for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }
}
