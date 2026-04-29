package com.passport.creditpassport.auth.exception;

public class NumberAlreadyExistsException extends DuplicateAuthExceptions {
    public NumberAlreadyExistsException(String UserNumber) {
        super("Phone number '" + UserNumber + "' already exists.");
    }
}
