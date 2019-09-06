package com.mastercard.labs.bps.discovery.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Address implements Serializable {
    private String street;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String zip;
    private String country;
}