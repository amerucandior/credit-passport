package com.passport.creditpassport.exception;

public class LenderAlreadyExistsException extends RuntimeException {
    public LenderAlreadyExistsException(String message) {
        super(message);
    }
}