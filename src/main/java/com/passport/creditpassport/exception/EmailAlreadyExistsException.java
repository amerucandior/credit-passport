package com.passport.creditpassport.exception;

public class EmailAlreadyExistsException extends DuplicateAuthExceptions {
    public EmailAlreadyExistsException(String UserEmail) {
        super("User with email " + UserEmail + " already exists");
    }
}
