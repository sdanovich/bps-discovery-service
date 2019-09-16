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
public class Discovery extends BpsEntity implements Record {

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
    @Enumerated(EnumType.STRING)
    private EXISTS bpsPresent;
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
    private Integer rating;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private STATUS status;
    @Enumerated(EnumType.STRING)
    private EXISTS found;
    @Column(columnDefinition = "TEXT")
    private String reason;
}
