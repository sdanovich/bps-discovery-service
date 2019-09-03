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
public class BusinessEntity implements Serializable {
    private String name;
    private String id;
    private Address address;
}