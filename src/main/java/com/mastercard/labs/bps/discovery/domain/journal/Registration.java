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
public class Registration extends BpsEntity implements Record {


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchFile.ENTITY entityType;
    private String bpsId;
    private String batchId;
    private String companyName;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String taxId;
    private String dbId;
    private String trackId;
    private String confidence;
    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Discovery.STATUS status;

    @Override
    public Discovery.EXISTS getFound() {
        return null;
    }

    @Override
    public void setFound(Discovery.EXISTS exists) {

    }


}
