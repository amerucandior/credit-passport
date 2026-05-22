package com.passport.creditpassport.scoring.dto;

public record ScoringResponse(
        Integer creditScore,
        String creditScoreBand
) {}
