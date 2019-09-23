package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;

@JsonPropertyOrder(value = {DiscoveryConst.SUPPLIER_ID, DiscoveryConst.ENFORCEMENT_TYPE, DiscoveryConst.MAX_AMT_LIMIT, DiscoveryConst.RELATIONSHIP,
        DiscoveryConst.STATUS, DiscoveryConst.REASON,
        DiscoveryConst.BUYER_TAX_ID_1, DiscoveryConst.BUYER_TAX_ID_2, DiscoveryConst.BUYER_TAX_ID_3, DiscoveryConst.BUYER_TAX_ID_4, DiscoveryConst.BUYER_TAX_ID_5,
        DiscoveryConst.BUYER_TAX_ID_6, DiscoveryConst.BUYER_TAX_ID_7, DiscoveryConst.BUYER_TAX_ID_8, DiscoveryConst.BUYER_TAX_ID_9, DiscoveryConst.BUYER_TAX_ID_10,
        DiscoveryConst.BUYER_TAX_ID_11, DiscoveryConst.BUYER_TAX_ID_12, DiscoveryConst.BUYER_TAX_ID_13, DiscoveryConst.BUYER_TAX_ID_14, DiscoveryConst.BUYER_TAX_ID_15,
        DiscoveryConst.BUYER_TAX_ID_16, DiscoveryConst.BUYER_TAX_ID_17, DiscoveryConst.BUYER_TAX_ID_18, DiscoveryConst.BUYER_TAX_ID_19, DiscoveryConst.BUYER_TAX_ID_20
})

@Getter
@Setter
@NoArgsConstructor
public class RuleRegistrationModel implements Serializable {
    @JsonProperty(value = DiscoveryConst.SUPPLIER_ID)
    private String supplierId;
    @JsonProperty(value = DiscoveryConst.ENFORCEMENT_TYPE)
    private String enforcementType;
    @JsonProperty(value = DiscoveryConst.MAX_AMT_LIMIT)
    private String maxAmtLimit;
    @JsonProperty(value = DiscoveryConst.RELATIONSHIP)
    private String relationship;

    @JsonProperty(value = DiscoveryConst.STATUS)
    private String status;
    @JsonProperty(value = DiscoveryConst.REASON)
    private String reason;

    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_1)
    private String buyerTaxId1;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_2)
    private String buyerTaxId2;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_3)
    private String buyerTaxId3;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_4)
    private String buyerTaxId4;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_5)
    private String buyerTaxId5;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_6)
    private String buyerTaxId6;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_7)
    private String buyerTaxId7;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_8)
    private String buyerTaxId8;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_9)
    private String buyerTaxId9;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_10)
    private String buyerTaxId10;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_11)
    private String buyerTaxId11;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_12)
    private String buyerTaxId12;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_13)
    private String buyerTaxId13;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_14)
    private String buyerTaxId14;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_15)
    private String buyerTaxId15;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_16)
    private String buyerTaxId16;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_17)
    private String buyerTaxId17;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_18)
    private String buyerTaxId18;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_19)
    private String buyerTaxId19;
    @JsonProperty(value = DiscoveryConst.BUYER_TAX_ID_20)
    private String buyerTaxId20;
}
