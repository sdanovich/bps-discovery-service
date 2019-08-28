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
public class Discovery extends BpsEntity {

    public enum STATUS {
        READY,
        COMPLETE,
        FAILED
    }

    public enum EXISTS {
        Y,
        N,
        I
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
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
    @Enumerated(EnumType.STRING)
    private EXISTS bpsPresent;
    private Integer rating;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private STATUS status;
    @Enumerated(EnumType.STRING)
    private EXISTS found;
    @Column(columnDefinition = "TEXT")
    private String reason;
}
