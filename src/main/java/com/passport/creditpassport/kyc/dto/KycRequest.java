package com.passport.creditpassport.kyc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record KycRequest(

        @Schema(description = "a selfie picture")
        String selfiePicture,

        @Schema(description = "front-side picture of your National Id")
        String nationalIdFront,

        @Schema(description = "back-side picture of your National Id")
        String nationalIdBack,

        @Schema(description = "KRA-pin is required for verification")
        String kraPin,

        @Schema(description = "3 months payslip required for verification")
        String latestPayslip
) {}
