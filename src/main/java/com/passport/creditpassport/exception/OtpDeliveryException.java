package com.passport.creditpassport.exception;

import lombok.Getter;

public class OtpDeliveryException extends RuntimeException {

    @Getter
    private final boolean retryable;

    public static OtpDeliveryException retryable(String message, Throwable cause) {
        return new OtpDeliveryException(message, cause, true);
    }

    public static OtpDeliveryException nonRetryable(String message, Throwable cause) {
        return new OtpDeliveryException(message, cause, false);
    }

    private OtpDeliveryException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }
}
