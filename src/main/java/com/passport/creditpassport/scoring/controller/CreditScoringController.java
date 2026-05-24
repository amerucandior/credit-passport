package com.passport.creditpassport.scoring.controller;

import com.passport.creditpassport.auth.AuthenticatedUserPrincipal;
import com.passport.creditpassport.creditpassport.CreditPassportService;
import com.passport.creditpassport.creditpassport.model.CreditPassport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Credit Passport", description = "Generate your Credit Passport or Get generated Credit Passport")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/passport")
@RequiredArgsConstructor
public class CreditScoringController {

    private final CreditPassportService creditPassportService;

    @Operation(summary = "Get passport", description = "Get passport for the current user")
    @GetMapping
    public ResponseEntity<CreditPassport> getPassport(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal
    ) {
        return ResponseEntity.ok(creditPassportService.getCreditPassport(principal.natId()));
    }

    @Operation(summary = "Generate passport", description = "Generate passport for the current user")
    @PostMapping("/generate")
    public ResponseEntity<CreditPassport> generate(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal
    ) {
        return ResponseEntity.ok(creditPassportService.generateOrRefreshPassport(principal.natId()));
    }
}
