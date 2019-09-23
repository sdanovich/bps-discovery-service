package com.mastercard.labs.bps.discovery.domain.journal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "journal")
public class Rules extends BpsEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    private String batchId;

    private String supplierId;
    private String enforcementType;
    private Integer maxAmtLimit;
    private String relationship;
    private String buyerTaxId1;
    private String buyerTaxId2;
    private String buyerTaxId3;
    private String buyerTaxId4;
    private String buyerTaxId5;
    private String buyerTaxId6;
    private String buyerTaxId7;
    private String buyerTaxId8;
    private String buyerTaxId9;
    private String buyerTaxId10;
    private String buyerTaxId11;
    private String buyerTaxId12;
    private String buyerTaxId13;
    private String buyerTaxId14;
    private String buyerTaxId15;
    private String buyerTaxId16;
    private String buyerTaxId17;
    private String buyerTaxId18;
    private String buyerTaxId19;
    private String buyerTaxId20;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Discovery.STATUS status;
    @Column(columnDefinition = "TEXT")
    private String reason;

}