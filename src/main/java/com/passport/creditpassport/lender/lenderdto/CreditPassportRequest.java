package com.passport.creditpassport.lender.lenderdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record CreditPassportRequest (

    @NotBlank
    String cbkLicenseNo,

    @NotBlank
    String nationalId
) {}
