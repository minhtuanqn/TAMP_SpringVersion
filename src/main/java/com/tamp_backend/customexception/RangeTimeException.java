package com.tamp_backend.customexception;

/**
 * Custom exception about a range time in application
 */
public class RangeTimeException extends RuntimeException{

    public RangeTimeException() {
    }

    public RangeTimeException(String message) {
        super(message);
    }

    public RangeTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RangeTimeException(Throwable cause) {
        super(cause);
    }

    public RangeTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
