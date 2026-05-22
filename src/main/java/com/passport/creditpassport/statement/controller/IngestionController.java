package com.passport.creditpassport.statement.controller;

import com.passport.creditpassport.auth.AuthenticatedUserPrincipal;
import com.passport.creditpassport.statement.dto.FinancialStatementResponse;
import com.passport.creditpassport.statement.dto.UploadRequest;
import com.passport.creditpassport.statement.enums.StatementType;
import com.passport.creditpassport.statement.service.FinancialStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Financial Statements Ingestion",
        description = "Allows M-Pesa, bank, and Sacco statement ingestion")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/statements")
@RequiredArgsConstructor
public class IngestionController {

    private final FinancialStatementService financialStatementService;

    @Operation(summary = "Upload a financial statement")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<FinancialStatementResponse> upload(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @RequestPart("file") @Parameter(schema = @Schema(type = "string", format = "binary")) MultipartFile file,
            @RequestParam("statementType") StatementType statementType,
            @RequestParam(value = "institutionHint",   required = false) String institutionHint,
            @RequestParam(value = "accountAlias",      required = false) String accountAlias,
            @RequestParam(value = "statementPassword", required = false) String statementPassword

    ) {
        FinancialStatementResponse response = financialStatementService.upload(principal.id(), file, statementType);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a financial statement by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FinancialStatementResponse> getById(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable UUID id) {
        return ResponseEntity.ok(financialStatementService.getById(principal.id(), id));
    }

    @Operation(summary = "List all financial statements for the authenticated user")
    @GetMapping
    public ResponseEntity<List<FinancialStatementResponse>> listMine(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntity.ok(financialStatementService.getAllByUser(principal.id()));
    }
}