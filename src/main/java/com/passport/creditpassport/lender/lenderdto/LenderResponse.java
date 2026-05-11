package com.passport.creditpassport.lender.lenderdto;

import lombok.Builder;

@Builder
public record LenderResponse (
    String apiKey,
    String message
 ) {}
