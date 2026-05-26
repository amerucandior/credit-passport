package com.passport.creditpassport.lender.lenderdto;

import jakarta.validation.constraints.NotBlank;


public record CreditPassportRequest (

    @NotBlank
    String cbkLicenseNo,

    @NotBlank
    String nationalId
) {}
