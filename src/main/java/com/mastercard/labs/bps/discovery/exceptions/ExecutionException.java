package com.mastercard.labs.bps.discovery.exceptions;

public class ExecutionException extends RuntimeException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Exception e) {
        super(message, e);
    }

}
