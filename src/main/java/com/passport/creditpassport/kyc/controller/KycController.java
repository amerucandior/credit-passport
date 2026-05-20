package com.passport.creditpassport.kyc.controller;

import com.passport.creditpassport.auth.AuthenticatedUserPrincipal;
import com.passport.creditpassport.kyc.dto.KycRequest;
import com.passport.creditpassport.kyc.dto.KycResponse;
import com.passport.creditpassport.kyc.service.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KycResponse> kycDoc (
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @RequestPart("file") MultipartFile file
    ) {
        KycResponse kycResponse = kycService.uploadKycDoc(principal.id(), file);
        return ResponseEntity.ok(kycResponse);
    }

}
