package com.tamp_backend.customexception;

/**
 * Custom exception for no such name of field in a class
 */
public class NotFoundFieldOfClassException extends ClassCustomException{

    public NotFoundFieldOfClassException(String message) {
        super(message);
    }
}
