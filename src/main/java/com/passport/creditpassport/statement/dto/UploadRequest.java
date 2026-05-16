package com.passport.creditpassport.statement.dto;

import com.passport.creditpassport.statement.enums.StatementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UploadRequest (

        @NotNull(message = "Statement type is required")
        @Schema(description = "Source of the statement",
                example = "MPESA",
                allowableValues = {"MPESA", "BANK", "SACCO"})
        StatementType statementType,

        @Schema(description = "name of statement provider",
                example = "Equity Bank, M-pesa, Stima Sacco")
        String institutionHint,

        @Schema(description = "account name e.g. salary account")
        String accountAlias,

        @Schema(description = "password for password locked statement e.g. M-pesa")
        String statementPassword
) {}
