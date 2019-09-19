package com.mastercard.labs.bps.discovery.exceptions;


import org.apache.log4j.LogManager;

public class TrackAccess5xxException extends RuntimeException {

    org.apache.log4j.Logger LOGGER = LogManager.getLogger(TrackAccess5xxException.class);

    public TrackAccess5xxException(String message) {
        super(message);
    }

    public TrackAccess5xxException(Object body, String message) {
        super(message);
        LOGGER.error("Error " + body);
    }

}
