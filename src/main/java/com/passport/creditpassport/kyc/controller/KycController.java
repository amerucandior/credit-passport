package com.passport.creditpassport.kyc.controller;

import com.passport.creditpassport.auth.AuthenticatedUserPrincipal;
import com.passport.creditpassport.kyc.dto.KycResponse;
import com.passport.creditpassport.kyc.enums.KycDocType;
import com.passport.creditpassport.kyc.service.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "KYC", description = "allows Know Your Customer operations")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @Operation(summary = "uploads a document for kyc")
    @PreAuthorize("hasRole('BORROWER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KycResponse> kycDoc (
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "kycDocType")KycDocType kycDocType
            ) {
        KycResponse kycResponse = kycService.uploadKycDoc(principal.id(), file, kycDocType);
        return ResponseEntity.ok(kycResponse);
    }

    @Operation(summary = "Get a KYC document by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<KycResponse> getKyc(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntity.ok(kycService.getKycDoc(principal.id()));
    }

    @Operation(summary = "Delete a KYC document by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteKyc(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        kycService.deleteKycDoc(principal.id());
        return ResponseEntity.noContent().build();
    }
}
