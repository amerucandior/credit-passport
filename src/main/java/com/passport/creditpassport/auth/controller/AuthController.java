package com.passport.creditpassport.auth.controller;

import com.passport.creditpassport.auth.dto.*;
import com.passport.creditpassport.auth.service.AuthService;
import com.passport.creditpassport.auth.serviceimplementation.LoginOtpImpl;
import com.passport.creditpassport.auth.serviceimplementation.RegistrationOtpImpl;
import com.passport.creditpassport.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Registration and login endpoints")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegistrationOtpImpl registrationOtpImpl;
    private final LoginOtpImpl loginOtpImpl;

    // -----------------------------------------------------------------------
    // Registration — two steps
    // -----------------------------------------------------------------------

    /**
     * Create the account and send a verification OTP.
     * Returns 200 with no body — no JWT yet, the account is not verified.
     */
    @Operation(summary = "Register a new user and send a verification OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created and verification OTP sent"),
            @ApiResponse(responseCode = "400", description = "Invalid registration details",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Name, national ID, phone number, or email already exists",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Account was created, but OTP email could not be sent",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        registrationOtpImpl.sendRegistrationOtp(request.email());
        return ResponseEntity.ok().build();
    }

    /**
     * Submit the OTP to verify the account.
     * Returns 200 with no body — user must now log in.
     */
    @Operation(summary = "Verify registration OTP and activate account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account verified"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Account is already verified",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PostMapping("/verify-registration")
    public ResponseEntity<Void> verifyRegistration(
            @Valid @RequestBody OtpRequest.VerifyOtpRequest req) {
        registrationOtpImpl.verifyRegistrationOtp(req.email(), req.otp());
        return ResponseEntity.ok().build();
    }

    /**
     * Resend the registration OTP when the original expires or is lost.
     */
    @Operation(summary = "Resend registration OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration OTP resent"),
            @ApiResponse(responseCode = "400", description = "Invalid email address",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Account is already verified",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "OTP email could not be sent",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PostMapping("/resend-registration-otp")
    public ResponseEntity<Void> resendRegistrationOtp(
            @Valid @RequestBody OtpRequest.EmailOnlyRequest req) {
        registrationOtpImpl.resendRegistrationOtp(req.email());
        return ResponseEntity.ok().build();
    }

    // -----------------------------------------------------------------------
    // Login — two steps
    // -----------------------------------------------------------------------

    /**
     * Validate credentials and send a login OTP.
     * Returns 200 with no body — no JWT yet.
     */
    @Operation(summary = "Initiate login: validate credentials and send login OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credentials accepted and login OTP sent"),
            @ApiResponse(responseCode = "400", description = "Invalid login request",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email/national ID or password",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Account is not verified",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "OTP email could not be sent",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        OtpPayload payload = loginOtpImpl.initiateLogin(request.identifier(), request.userPassword());
        authService.sendLoginEmailVerificationOtp(payload.email(), payload.otpCode());
        return ResponseEntity.ok().build();
    }

    /**
     * Submit the OTP to complete login and receive a JWT.
     */
    @Operation(summary = "Verify login OTP and receive JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login verified and JWT issued"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiErrorResponse.class)))
    })
    @PostMapping("/verify-login")
    public ResponseEntity<AuthResponse> verifyLogin(
            @Valid @RequestBody OtpRequest.VerifyOtpRequest req) {
        AuthResponse response = loginOtpImpl.verifyLoginOtp(req.email(), req.otp());
        return ResponseEntity.ok(response);
    }
}