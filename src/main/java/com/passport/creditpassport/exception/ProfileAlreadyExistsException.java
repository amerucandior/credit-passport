package com.passport.creditpassport.exception;

public class ProfileAlreadyExistsException extends RuntimeException {
    public ProfileAlreadyExistsException(String userId) {
        super("Profile already exists for user " + userId);
    }
}
