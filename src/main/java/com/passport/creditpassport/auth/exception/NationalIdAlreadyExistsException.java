package com.passport.creditpassport.auth.exception;

public class NationalIdAlreadyExistsException extends DuplicateAuthExceptions {
    public NationalIdAlreadyExistsException(String UserNationalId) {
        super("National ID '" + UserNationalId + "' already exists.");
    }
}
