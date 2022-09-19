package com.tamp_backend.customexception;

/**
 * Custom exception for unauthorization entity
 */
public class UnauthorizationException extends RuntimeException{
    public UnauthorizationException() {
    }

    public UnauthorizationException(String message) {
        super(message);
    }

    public UnauthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizationException(Throwable cause) {
        super(cause);
    }

    public UnauthorizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
