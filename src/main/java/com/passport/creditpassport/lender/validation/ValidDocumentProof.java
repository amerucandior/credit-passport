package com.passport.creditpassport.lender.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = DocumentProofValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidDocumentProof {
    String message() default "Name Approval Proof is required when Certificate of Incorporation is not provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}