package com.passport.creditpassport.exception;

public class InvalidProfilePhotoUrlException extends RuntimeException {
    public InvalidProfilePhotoUrlException() {
        super("Profile photo URL is invalid or uses a disallowed scheme.");
    }
}
