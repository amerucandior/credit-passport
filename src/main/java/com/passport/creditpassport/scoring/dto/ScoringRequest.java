package com.passport.creditpassport.scoring.dto;

import com.passport.creditpassport.userprofile.models.EmploymentStatus;
import com.passport.creditpassport.userprofile.models.Gender;

import java.math.BigDecimal;
import java.util.List;

public record ScoringRequest(
        String id,
        String natId,
        Gender gender,
        boolean isVerified,
        EmploymentStatus employmentStatus,
        BigDecimal monthlyIncomeKes,
        List<StatementPayload> statements
) {}
