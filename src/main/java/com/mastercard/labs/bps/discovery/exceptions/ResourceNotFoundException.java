package com.mastercard.labs.bps.discovery.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import java.util.Arrays;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ResourceNotFoundException extends RuntimeException {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceNotFoundException.class);

    private String[] messages;
    private HttpStatus status;

    public ResourceNotFoundException(String message) {
        this(NOT_FOUND, message);
    }



    public ResourceNotFoundException( HttpStatus status, String... messages) {
        super(Arrays.deepToString(messages));
        this.status = status;
        this.messages = messages;
        LOGGER.info(Arrays.deepToString(messages));
    }

    public ResourceNotFoundException(Set<ConstraintViolation<Object>> violations, HttpStatus badRequest) {
        this(badRequest, violations.stream().map(ConstraintViolation::getMessage).toArray(String[]::new));
    }

    @Override
    public String getMessage() {
        return Arrays.deepToString(messages);
    }

    public String[] getMessages() {
        return messages;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
