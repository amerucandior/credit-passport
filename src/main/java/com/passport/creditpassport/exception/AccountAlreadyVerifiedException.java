package com.passport.creditpassport.exception;

public class AccountAlreadyVerifiedException extends RuntimeException {
    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
}
