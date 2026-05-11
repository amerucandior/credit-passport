package com.passport.creditpassport.lender.validation;

import com.passport.creditpassport.lender.lenderdto.LenderRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentProofValidator implements ConstraintValidator<ValidDocumentProof, LenderRequest> {

    @Override
    public boolean isValid(LenderRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        boolean hasCertOfIncorporation = request.certificateOfIncorporation() != null
                && !request.certificateOfIncorporation().isBlank();

        boolean hasNameApprovalProof = request.nameApprovalProof() != null
                && !request.nameApprovalProof().isBlank();

        if (!hasCertOfIncorporation && !hasNameApprovalProof) {
            // Points the error at nameApprovalProof field instead of the whole object
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Name Approval Proof is required when Certificate of Incorporation is not provided"
                    )
                    .addPropertyNode("nameApprovalProof")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}