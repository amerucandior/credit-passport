package com.passport.creditpassport.exception;

public class LenderAlreadyExistsException extends DuplicateAuthExceptions {
    public LenderAlreadyExistsException(String message) {
        super(message);
    }
}