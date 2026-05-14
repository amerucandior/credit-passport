package com.passport.creditpassport.statement.dto;

import com.passport.creditpassport.statement.enums.StatementType;
import com.passport.creditpassport.statement.models.Statement;
import java.util.UUID;

import java.time.Instant;

public record FinancialStatementResponse(
        UUID id,
        String userId,
        StatementType statementType,
        String originalFilename,
        String statementUrl,
        boolean parsed,
        Instant createdAt
) {
    public static FinancialStatementResponse from(Statement s) {
        return new FinancialStatementResponse(
                s.getId(),
                s.getUserId(),
                s.getStatementType(),
                s.getOriginalFilename(),
                s.getStatementUrl(),
                s.isParsed(),
                s.getCreatedAt()
        );
    }
}