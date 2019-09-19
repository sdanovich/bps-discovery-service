package com.mastercard.labs.bps.discovery.exceptions;


import org.apache.log4j.LogManager;

public class TrackAccess4xxException extends RuntimeException {

    org.apache.log4j.Logger LOGGER = LogManager.getLogger(TrackAccess4xxException.class);

    public TrackAccess4xxException(String message) {
        super(message);
    }

    public TrackAccess4xxException(Object body, String message) {
        super(message);
        LOGGER.error("Error " + body);
    }

}
