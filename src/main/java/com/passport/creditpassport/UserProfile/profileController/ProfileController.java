package com.passport.creditpassport.UserProfile.profileController;

import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;
import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.service.ProfileService;
import com.passport.creditpassport.auth.models.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")       // tells Swagger this endpoint needs a JWT
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * @AuthenticationPrincipal unwraps the principal stored in SecurityContextHolder
     * by JwtAuthenticationFilter — which is the full `user` entity.
     * The controller never touches userId strings; the service owns that logic.
     */

    @Operation(summary = "Create a profile")
    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.createProfile(authenticatedUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get your profile")
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(profileService.getProfile(authenticatedUser));
    }

    @Operation(summary = "Update your profile")
    @PatchMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(authenticatedUser, request));
    }
}