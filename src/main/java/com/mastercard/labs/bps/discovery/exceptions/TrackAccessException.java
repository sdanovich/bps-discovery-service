package com.mastercard.labs.bps.discovery.exceptions;


import org.apache.log4j.LogManager;

public class TrackAccessException extends RuntimeException {

    org.apache.log4j.Logger LOGGER = LogManager.getLogger(TrackAccessException.class);

    public TrackAccessException(String message) {
        super(message);
    }

    public TrackAccessException(Object body, String message) {
        super(message);
        LOGGER.error("Error " + body);
    }

}
