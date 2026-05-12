package com.passport.creditpassport.lender.exceptions;

public class LenderAlreadyExistsException extends RuntimeException {
    public LenderAlreadyExistsException(String message) {
        super(message);
    }
}