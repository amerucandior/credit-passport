package com.passport.creditpassport.exception;

public class NumberAlreadyExistsException extends DuplicateAuthExceptions {
    public NumberAlreadyExistsException(String UserNumber) {
        super("Phone number '" + UserNumber + "' already exists.");
    }
}
