package com.passport.creditpassport.exception;

public class NameAlreadyExistsException extends DuplicateAuthExceptions {
    public NameAlreadyExistsException(String UserName) {
        super("User with name " + UserName + " already exists");
    }
}
