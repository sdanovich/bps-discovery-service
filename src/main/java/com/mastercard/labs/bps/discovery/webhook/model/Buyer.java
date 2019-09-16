package com.mastercard.labs.bps.discovery.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Buyer {
    private String bpsId;
    private String name;
}