package com.mastercard.labs.bps.discovery.exceptions;

public class SignatureVerificationException extends RuntimeException {

    public SignatureVerificationException(String message) {
        super(message);
    }
}
