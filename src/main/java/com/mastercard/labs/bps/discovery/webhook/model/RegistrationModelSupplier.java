package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;

@JsonPropertyOrder(value = {DiscoveryConst.COMPANY_NAME, DiscoveryConst.ADDRESS_LINE_1, DiscoveryConst.ADDRESS_LINE_2, DiscoveryConst.ADDRESS_LINE_3,
        DiscoveryConst.CITY, DiscoveryConst.STATE_PROVINCE, DiscoveryConst.COUNTRY, DiscoveryConst.ZIP, DiscoveryConst.SUPPLIER_ID, DiscoveryConst.TAX_ID, DiscoveryConst.DB_ID,
        DiscoveryConst.STATUS, DiscoveryConst.REASON})
@Getter
@Setter
@NoArgsConstructor
public class RegistrationModelSupplier implements Serializable, Model {
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
    @JsonProperty(value = DiscoveryConst.SUPPLIER_ID)
    private String supplierId;
    @JsonProperty(value = DiscoveryConst.TAX_ID)
    private String taxId;
    @JsonProperty(value = DiscoveryConst.DB_ID)
    private String dbId;
    @JsonProperty(value = DiscoveryConst.STATUS)
    private String status;
    @JsonProperty(value = DiscoveryConst.REASON)
    private String reason;
}
