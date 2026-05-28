package com.passport.creditpassport.userprofile.serviceImplementation;

import com.passport.creditpassport.service.CloudinaryService;
import com.passport.creditpassport.userprofile.dto.ProfileResponse;
import com.passport.creditpassport.userprofile.dto.UpdateProfileRequest;
import com.passport.creditpassport.userprofile.repository.ProfileRepository;
import com.passport.creditpassport.userprofile.service.ProfileService;
import com.passport.creditpassport.userprofile.models.UserProfile;
import com.passport.creditpassport.exception.ProfileAlreadyExistsException;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private static final long   MAX_PHOTO_SIZE_BYTES = 5 * 1024 * 1024L;           // 5 MB
    private static final Set<String> ALLOWED_MIME_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private final ProfileRepository  profileRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public ProfileResponse createProfile(String userId, String name,String email, String number, String natId, UpdateProfileRequest request) {

        if (profileRepository.existsByUserId(userId)) {
            throw new ProfileAlreadyExistsException(userId);
        }

        UserProfile userProfile = getUserProfile(request, userId);

        UserProfile saved = profileRepository.save(userProfile);
        log.info("Profile created for userId {}", userId);
        return ProfileResponse.fromEntity(saved, name, email, number, natId);
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
    public ProfileResponse getProfile(String userId, String name, String email, String number, String natId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return ProfileResponse.fromEntity(profile, name, email, number, natId);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(String userId, String name, String email, String number, String natId, UpdateProfileRequest request) {
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
        return ProfileResponse.fromEntity(saved, name, email, number, natId);
    }

    @Override
    @Transactional
    public ProfileResponse uploadProfilePhoto(String userId, String name, String email,
                                              String number, String natId,
                                              MultipartFile file) {
        validateImageFile(file);

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found — create one first."));

        Map<String, Object> uploadResult = cloudinaryService.uploadFile(
                file,
                "credit-passport/profile-photos",
                userId                          // deterministic publicId → overwrite is idempotent
        );

        String secureUrl = (String) uploadResult.get("secure_url");
        profile.setProfilePhotoUrl(secureUrl);

        UserProfile saved = profileRepository.save(profile);
        log.info("Profile photo uploaded for userId {}", userId);
        return ProfileResponse.fromEntity(saved, name, email, number, natId);
    }

    private static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Photo file must not be empty.");
        }
        String mime = file.getContentType();
        if (mime == null || !ALLOWED_MIME_TYPES.contains(mime)) {
            throw new IllegalArgumentException(
                    "Unsupported file type. Allowed: JPEG, PNG, WebP.");
        }
        if (file.getSize() > MAX_PHOTO_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "File exceeds 5 MB limit.");
        }
    }
}
