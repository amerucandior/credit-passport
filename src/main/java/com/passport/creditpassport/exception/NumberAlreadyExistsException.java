package com.passport.creditpassport.exception;

public class NumberAlreadyExistsException extends DuplicateAuthExceptions {
    public NumberAlreadyExistsException(String UserNumber) {
        super("National ID '" + UserNumber + "' already exists.");
    }
}
