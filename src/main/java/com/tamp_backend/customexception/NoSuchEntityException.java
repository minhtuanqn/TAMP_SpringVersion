package com.tamp_backend.customexception;

/**
 * Custom exception for no such entity
 */
public class NoSuchEntityException extends SQLCustomException{

    public NoSuchEntityException(String message) {
        super(message);
    }
}
