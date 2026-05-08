package com.passport.creditpassport.lender.lenderdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditPassportRequest {

    @NotBlank
    private String cbkLicenseNo;

    @NotBlank
    private String nationalId;
}
