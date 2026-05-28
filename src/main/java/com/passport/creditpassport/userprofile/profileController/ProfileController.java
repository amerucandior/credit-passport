package com.passport.creditpassport.userprofile.profileController;

import com.passport.creditpassport.auth.AuthenticatedUserPrincipal;
import com.passport.creditpassport.userprofile.dto.UpdateProfileRequest;
import com.passport.creditpassport.userprofile.dto.ProfileResponse;
import com.passport.creditpassport.userprofile.service.ProfileService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Profile", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")       // tells Swagger this endpoint needs a JWT
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


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
    @PreAuthorize("hasRole('BORROWER')")
    @PostMapping
    @SuppressWarnings("java:S5131") // XSS: Jackson serializes all string output as JSON-encoded — no raw HTML rendering
    public ResponseEntity<ProfileResponse> createProfile(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.createProfile(
                principal.id(),
                principal.name(),
                principal.email(),
                principal.number(),
                principal.natId(),
                request);
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
    @PreAuthorize("hasRole('BORROWER')")
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntity.ok(profileService.getProfile(
                principal.id(),
                principal.name(),
                principal.email(),
                principal.number(),
                principal.natId()
        ));
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
    @PreAuthorize("hasRole('BORROWER')")
    @PatchMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(
                principal.id(),
                principal.name(),
                principal.email(),
                principal.number(),
                principal.natId(),
                request));
    }

    @Operation(summary = "Upload your profile photo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file (wrong type, too large, or empty)",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authorised for this resource",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found for authenticated user",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PreAuthorize("hasRole('BORROWER')")
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponse> uploadProfilePhoto(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        ProfileResponse response = profileService.uploadProfilePhoto(
                principal.id(),
                principal.name(),
                principal.email(),
                principal.number(),
                principal.natId(),
                file);
        return ResponseEntity.ok(response);
    }
}
