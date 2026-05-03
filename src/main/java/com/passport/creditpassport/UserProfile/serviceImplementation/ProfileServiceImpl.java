package com.passport.creditpassport.UserProfile.serviceImplementation;

import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;
import com.passport.creditpassport.UserProfile.repository.ProfileRepository;
import com.passport.creditpassport.UserProfile.service.ProfileService;
import com.passport.creditpassport.UserProfile.models.UserProfile;
import com.passport.creditpassport.auth.models.User;
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
                throw new IllegalArgumentException("Invalid URL scheme: " + scheme);
            }
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed profile photo URL");
        }
    }


    @Override
    @Transactional
    public ProfileResponse createProfile(User authenticatedUser, UpdateProfileRequest request) {

        String userId = authenticatedUser.getId();

        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("UserProfile already exists!");
        }

        UserProfile userProfile = getUserProfile(request, userId);

        UserProfile saved = profileRepository.save(userProfile);
        log.info("Profile created for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }

    private static @NonNull UserProfile getUserProfile(UpdateProfileRequest request, String userId) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setGender(request.getGender());
        userProfile.setDateOfBirth(request.getDateOfBirth());
        userProfile.setEmployerName(
                StringEscapeUtils.escapeHtml4(request.getEmployerName())); // Sanitize free text from xss
        userProfile.setEmploymentStatus(request.getEmploymentStatus());
        userProfile.setOccupation(
                StringEscapeUtils.escapeHtml4(request.getOccupation()));
        userProfile.setMonthlyIncomeKes(request.getMonthlyIncomeKes());
        userProfile.setProfilePhotoUrl(
                sanitizeUrl(request.getProfilePhotoUrl()));                 // URL — special case
        return userProfile;
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
