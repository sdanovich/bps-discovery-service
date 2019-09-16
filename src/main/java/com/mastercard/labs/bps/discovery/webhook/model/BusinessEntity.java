package com.mastercard.labs.bps.discovery.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessEntity {
    private String name;
    private String trackId;
    private String taxId;
    private Address address;
}

