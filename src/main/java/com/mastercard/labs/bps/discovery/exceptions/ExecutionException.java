package com.mastercard.labs.bps.discovery.exceptions;

import java.util.Arrays;

public class ExecutionException extends RuntimeException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String... messages) {
        super(Arrays.deepToString(messages));
    }

    public ExecutionException(String message, Exception e) {
        super(message, e);
    }

}
