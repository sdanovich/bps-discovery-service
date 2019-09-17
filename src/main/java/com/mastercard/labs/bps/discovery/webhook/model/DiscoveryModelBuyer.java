package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;

@JsonPropertyOrder(value = {DiscoveryConst.COMPANY_NAME, DiscoveryConst.ADDRESS_LINE_1, DiscoveryConst.ADDRESS_LINE_2, DiscoveryConst.ADDRESS_LINE_3,
        DiscoveryConst.CITY, DiscoveryConst.STATE_PROVINCE, DiscoveryConst.COUNTRY, DiscoveryConst.ZIP, DiscoveryConst.TAX_ID, DiscoveryConst.DB_ID,
        DiscoveryConst.BPS_AVAILABLE, DiscoveryConst.CONFIDENCE, DiscoveryConst.TRACK_ID, DiscoveryConst.TRACK_SCORE, DiscoveryConst.TRACK_BUSINESSNAME,
        DiscoveryConst.TRACK_STREET_ADDRESS, DiscoveryConst.TRACK_CITY, DiscoveryConst.TRACK_STATE, DiscoveryConst.TRACK_ZIP})
@Getter
@Setter
@NoArgsConstructor
public class DiscoveryModelBuyer implements Serializable, Model {
    @JsonProperty(value = DiscoveryConst.COMPANY_NAME)
    private String companyName;
    @JsonProperty(value = DiscoveryConst.ADDRESS_LINE_1)
    private String address1;
    @JsonProperty(value = DiscoveryConst.ADDRESS_LINE_2)
    private String address2;
    @JsonProperty(value = DiscoveryConst.ADDRESS_LINE_3)
    private String address3;
    @JsonProperty(value = DiscoveryConst.CITY)
    private String city;
    @JsonProperty(value = DiscoveryConst.STATE_PROVINCE)
    private String state;
    @JsonProperty(value = DiscoveryConst.COUNTRY)
    private String country;
    @JsonProperty(value = DiscoveryConst.ZIP)
    private String zip;
    @JsonProperty(value = DiscoveryConst.TAX_ID)
    private String taxId;
    @JsonProperty(value = DiscoveryConst.DB_ID)
    private String dbId;
    @JsonProperty(value = DiscoveryConst.BPS_AVAILABLE)
    private String bpsAvailable;
    @JsonProperty(value = DiscoveryConst.CONFIDENCE)
    private String confidence;

    @JsonProperty(value = DiscoveryConst.TRACK_ID)
    private String trackId;

    @JsonProperty(value = DiscoveryConst.TRACK_SCORE)
    private String score;

    @JsonProperty(value = DiscoveryConst.TRACK_BUSINESSNAME)
    private String businessName;

    @JsonProperty(value = DiscoveryConst.TRACK_STREET_ADDRESS)
    private String streetAddress;

    @JsonProperty(value = DiscoveryConst.TRACK_CITY)
    private String trackCity;

    @JsonProperty(value = DiscoveryConst.TRACK_STATE)
    private String trackState;

    @JsonProperty(value = DiscoveryConst.TRACK_ZIP)
    private String trackZip;

}