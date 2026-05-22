package com.passport.creditpassport.scoring.mapper;

import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.scoring.dto.ScoringRequest;
import com.passport.creditpassport.scoring.dto.StatementPayload;
import com.passport.creditpassport.statement.dto.FinancialStatementResponse;
import com.passport.creditpassport.userprofile.models.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoringRequestMapper {

    public ScoringRequest toScoringRequest(
            User user,
            UserProfile profile,
            List<FinancialStatementResponse> statements,
            boolean kycVerified
    ) {
        List<StatementPayload> payloads = toStatementPayloads(statements);

        return new ScoringRequest(
                user.getId(),
                user.getNatId(),
                profile.getGender(),
                kycVerified,
                profile.getEmploymentStatus(),
                profile.getMonthlyIncomeKes(),
                payloads
        );
    }

    private List<StatementPayload> toStatementPayloads(List<FinancialStatementResponse> statements) {
        if (statements == null || statements.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one financial statement is required for credit scoring"
            );
        }

        List<StatementPayload> payloads = statements.stream()
                .filter(statement -> statement.statementType() != null)
                .filter(statement -> statement.statementUrl() != null && !statement.statementUrl().isBlank())
                .map(statement -> new StatementPayload(statement.statementType(), statement.statementUrl()))
                .toList();

        if (payloads.isEmpty()) {
            throw new IllegalArgumentException(
                    "No usable financial statements found. Each statement must have a type and URL."
            );
        }

        return payloads;
    }
}
