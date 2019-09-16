package com.mastercard.labs.bps.discovery.domain.journal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "journal")
public class BatchFile extends BpsEntity {

    public enum STATUS {
        RECEIVED,
        PROCESSING,
        COMPLETE,
        INVALID_FILE
    }

    public enum TYPE {
        LOOKUP,
        REGISTRATION
    }

    public enum ENTITY {
        BUYER,
        SUPPLIER
    }

    public BatchFile(String fileName, byte[] content, STATUS status) {
        this.fileName = fileName;
        this.content = content;
        this.status = status;
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(insertable = false, updatable = false)
    private String id;
    @Column(nullable = false, unique = true)
    private String fileName;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(nullable = false)
    private byte[] content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TYPE type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ENTITY entityType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private STATUS status;
    private String agentName;

    public BatchFile withStatus(STATUS status) {
        setStatus(status);
        return this;
    }
}
