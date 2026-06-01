package com.passport.creditpassport.lender.lendercontroller;

import com.passport.creditpassport.creditpassport.CreditPassportService;
import com.passport.creditpassport.lender.lenderdto.CreditPassportRequest;
import com.passport.creditpassport.lender.lenderdto.LenderCreditPassportResponse;
import com.passport.creditpassport.lender.lenderdto.LenderRequest;
import com.passport.creditpassport.lender.lenderdto.LenderResponse;
import com.passport.creditpassport.lender.lendermodel.Lender;
import com.passport.creditpassport.lender.lenderservice.LenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lender", description = "Lender registration and credit passport lookup")
@RestController
@RequestMapping("/api/lender")
@RequiredArgsConstructor
public class LenderController {

    private final LenderService lenderService;
    private final CreditPassportService creditPassportService;

    @Operation(summary = "Register a lender")
    @PostMapping("/register")
    public ResponseEntity<LenderResponse> register(@Valid @RequestBody LenderRequest request) {
        LenderResponse response = lenderService.registerLender(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Fetch credit passport by national ID for an authenticated lender")
    @SecurityRequirement(name = "apiKeyAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passport retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LenderCreditPassportResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid, or inactive API key"),
            @ApiResponse(responseCode = "404", description = "No passport found for supplied national ID")
    })
    @PreAuthorize("hasRole('LENDER')")
    @PostMapping("/credit-passport")
    public ResponseEntity<LenderCreditPassportResponse> getCreditPassport(
            @Valid @RequestBody CreditPassportRequest req,
            HttpServletRequest httpRequest) {

        Lender lender = (Lender) httpRequest.getAttribute("authenticatedLender");

        if (!lender.getCbkLicenseNo().equals(req.cbkLicenseNo())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                LenderCreditPassportResponse.fromEntity(
                        creditPassportService.getCreditPassport(req.nationalId())
                )
        );
    }

    @Operation(summary = "Lender API health check")
    @SecurityRequirement(name = "apiKeyAuth")
    @PreAuthorize("hasRole('LENDER')")
    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}