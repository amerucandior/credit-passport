package com.passport.creditpassport.UserProfile.profileController;

import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;
import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.service.ProfileService;
import com.passport.creditpassport.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")       // tells Swagger this endpoint needs a JWT
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Pulls the authenticated user identifier without coupling this module to auth internals.
     */
    @Operation(summary = "Create a profile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid profile data",
                    content = @Content(schema = @Schema(oneOf = {
                            GlobalExceptionHandler.ApiErrorResponse.class,
                            GlobalExceptionHandler.ValidationErrorResponse.class
                    }))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authorised for this resource",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Profile already exists for this user",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PostMapping
    @SuppressWarnings("java:S5131") // XSS sanitized in ProfileService.createProfile() via StringEscapeUtils + OWASP policy
    public ResponseEntity<ProfileResponse> createProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.createProfile(resolveAuthenticatedUserId(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get your profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fetched profile successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authorised for this resource",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found for authenticated user",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication) {
        return ResponseEntity.ok(profileService.getProfile(resolveAuthenticatedUserId(authentication)));
    }

    @Operation(summary = "Update your profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid profile data",
                    content = @Content(schema = @Schema(oneOf = {
                            GlobalExceptionHandler.ApiErrorResponse.class,
                            GlobalExceptionHandler.ValidationErrorResponse.class
                    }))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authorised for this resource",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found for authenticated user",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PatchMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(resolveAuthenticatedUserId(authentication), request));
    }

    private String resolveAuthenticatedUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        try {
            return (String) principal.getClass().getMethod("getId").invoke(principal);
        } catch (ReflectiveOperationException ignored) {
            return authentication.getName();
        }
    }
}
