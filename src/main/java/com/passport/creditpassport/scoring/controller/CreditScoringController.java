package com.passport.creditpassport.scoring.controller;

import com.passport.creditpassport.auth.AuthenticatedUserPrincipal;
import com.passport.creditpassport.creditpassport.CreditPassportService;
import com.passport.creditpassport.creditpassport.dto.CreditPassportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Credit Passport", description = "Generate your Credit Passport or retrieve an existing one")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/passport")
@RequiredArgsConstructor
public class CreditScoringController {

    private final CreditPassportService creditPassportService;

    @Operation(summary = "Get passport", description = "Retrieve the current user's credit passport")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passport retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CreditPassportResponse.class))),
            @ApiResponse(responseCode = "404", description = "No passport found for this user")
    })
    @PreAuthorize("hasRole('BORROWER')")
    @GetMapping
    public ResponseEntity<CreditPassportResponse> getPassport(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                CreditPassportResponse.fromEntity(
                        creditPassportService.getCreditPassport(principal.natId())
                )
        );
    }

    @Operation(summary = "Generate passport", description = "Generate or refresh the current user's credit passport")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passport generated successfully",
                    content = @Content(schema = @Schema(implementation = CreditPassportResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing profile or financial statements"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('BORROWER')")
    @PostMapping("/generate")
    public ResponseEntity<CreditPassportResponse> generate(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                CreditPassportResponse.fromEntity(
                        creditPassportService.generateOrRefreshPassport(principal.natId())
                )
        );
    }
}