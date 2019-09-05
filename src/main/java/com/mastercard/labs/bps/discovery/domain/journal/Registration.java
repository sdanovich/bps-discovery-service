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
    String companyName;
    String address1;
    String address2;
    String address3;
    String address4;
    String city;
    String state;
    String country;
    String zip;
    String taxId;
    String dbId;
    String trackId;
    String confidence;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Discovery.STATUS status;

    @Override
    public String getReason() {
        return null;
    }

    @Override
    public void setReason(String reason) {

    }

    @Override
    public Discovery.EXISTS getFound() {
        return null;
    }

    @Override
    public void setFound(Discovery.EXISTS exists) {

    }


}
