package com.mastercard.labs.bps.discovery.exceptions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExecutionException extends RuntimeException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String... messages) {
        super(Stream.of(messages).collect(Collectors.joining(", ")));
    }

}