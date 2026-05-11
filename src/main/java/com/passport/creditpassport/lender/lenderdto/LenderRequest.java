package com.passport.creditpassport.lender.lenderdto;

import com.passport.creditpassport.lender.validation.ValidDocumentProof;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ValidDocumentProof
public record LenderRequest(
        @Schema(description = "Lender name")
        @NotBlank
        String lenderName,

        @Schema(description = "cbk license number provided by the CBK")
        @NotBlank
        String cbkLicenseNo,

        @Schema(description = "Certificate of Incorporation issued by the Registrar of Companies under the Companies Act (Cap 486)")
        String certificateOfIncorporation,

        @Schema(description = "Memorandum Articles of Association sets the company's identity and scope. treated as the primary lens for assessing whether your entity is structurally fit to lend.")
        @NotBlank
        String memorandumArticlesOfAssociation,

        @Schema(description = "Registered business address of the company")
        @NotBlank
        String regBusinessAddress,

        @Schema(description = "Name approval proof provided IF certificate of incorporation is missing")
        String nameApprovalProof
) {}
