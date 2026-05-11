package com.passport.creditpassport.UserProfile.serviceImplementation;

import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;
import com.passport.creditpassport.UserProfile.repository.ProfileRepository;
import com.passport.creditpassport.UserProfile.service.ProfileService;
import com.passport.creditpassport.UserProfile.models.UserProfile;
import com.passport.creditpassport.auth.models.User;
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
    public ProfileResponse createProfile(User authenticatedUser, UpdateProfileRequest request) {

        String userId = authenticatedUser.getId();

        if (profileRepository.existsByUser_Id(userId)) {
            throw new ProfileAlreadyExistsException(userId);
        }

        UserProfile userProfile = getUserProfile(request, authenticatedUser);

        UserProfile saved = profileRepository.save(userProfile);
        log.info("Profile created for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }

    private static @NonNull UserProfile getUserProfile(UpdateProfileRequest request, User authenticatedUser) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(authenticatedUser);
        userProfile.setGender(request.getGender());
        userProfile.setDateOfBirth(request.getDateOfBirth());
        userProfile.setEmployerName(
                StringEscapeUtils.escapeHtml4(request.getEmployerName()));
        userProfile.setEmploymentStatus(request.getEmploymentStatus());
        userProfile.setOccupation(
                StringEscapeUtils.escapeHtml4(request.getOccupation()));
        userProfile.setSaccoName(
                StringEscapeUtils.escapeHtml4(request.getSaccoName()));
        userProfile.setMonthlyIncomeKes(request.getMonthlyIncomeKes());
        userProfile.setProfilePhotoUrl(
                sanitizeUrl(request.getProfilePhotoUrl()));
        return userProfile;
    }

    //    Read user profile
    @Transactional(readOnly = true)
    @Override
    public ProfileResponse getProfile(User authenticatedUser) {
        String userId = authenticatedUser.getId();

        UserProfile profile = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return ProfileResponse.fromEntity(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(User authenticatedUser, UpdateProfileRequest request) {
        String userId = authenticatedUser.getId();

        UserProfile profile = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found — create one first."));

        // Only overwrite fields that were actually supplied
        if (request.getGender()           != null) profile.setGender(request.getGender());
        if (request.getDateOfBirth()      != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getEmployerName()    != null) profile.setEmployerName(
                StringEscapeUtils.escapeHtml4(request.getEmployerName()));
        if (request.getOccupation()      != null) profile.setOccupation(
                StringEscapeUtils.escapeHtml4(request.getOccupation()));
        if (request.getSaccoName()       != null) profile.setSaccoName(
                StringEscapeUtils.escapeHtml4(request.getSaccoName()));
        if (request.getEmploymentStatus() != null) profile.setEmploymentStatus(request.getEmploymentStatus());
        if (request.getMonthlyIncomeKes() != null) profile.setMonthlyIncomeKes(request.getMonthlyIncomeKes());
        if (request.getProfilePhotoUrl() != null) profile.setProfilePhotoUrl(
                sanitizeUrl(request.getProfilePhotoUrl()));
        UserProfile saved = profileRepository.save(profile);
        log.info("Profile updated for userId {}", userId);
        return ProfileResponse.fromEntity(saved);
    }
}
