package com.mastercard.labs.bps.discovery.exceptions;


import org.apache.log4j.LogManager;

public class TrackAccess3xxException extends RuntimeException {

    org.apache.log4j.Logger LOGGER = LogManager.getLogger(TrackAccess3xxException.class);

    public TrackAccess3xxException(String message) {
        super(message);
    }

    public TrackAccess3xxException(Object body, String message) {
        super(message);
        LOGGER.error("Error " + body);
    }

}
