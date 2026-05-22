package com.passport.creditpassport.scoring.dto;

import com.passport.creditpassport.statement.enums.StatementType;

public record StatementPayload(
        StatementType statementType,
        String statementUrl
) {}
