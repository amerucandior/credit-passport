package com.passport.creditpassport.exception;

public class OtpMaxAttemptsExceededException extends RuntimeException {
    public OtpMaxAttemptsExceededException(String message) {
        super(message);
    }
}
