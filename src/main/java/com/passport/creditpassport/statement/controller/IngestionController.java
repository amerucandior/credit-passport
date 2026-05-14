package com.passport.creditpassport.statement.controller;

import com.passport.creditpassport.statement.dto.FinancialStatementResponse;
import com.passport.creditpassport.statement.dto.UploadRequest;
import com.passport.creditpassport.statement.service.FinancialStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Financial Statements Ingestion",
        description = "Allows M-Pesa, bank, and Sacco statement ingestion")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/statements")
@RequiredArgsConstructor
public class IngestionController {

    private final FinancialStatementService financialStatementService;

    @Operation(summary = "Upload a financial statement")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FinancialStatementResponse> upload(
            Authentication authentication,
            @RequestPart("file")  MultipartFile file,
            @RequestPart("data") @Valid UploadRequest request
    ) {
        String userId = resolveAuthenticatedUserId(authentication);

        FinancialStatementResponse response = financialStatementService.upload(
                userId,
                file,
                request.statementType()
        );

        return ResponseEntity.ok(response);
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