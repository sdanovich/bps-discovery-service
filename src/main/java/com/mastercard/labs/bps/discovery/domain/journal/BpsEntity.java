package com.mastercard.labs.bps.discovery.domain.journal;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


@MappedSuperclass
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BpsEntity {

    @CreatedBy
    protected String createdBy;
    @CreatedDate
    protected LocalDateTime creationDate;
    @LastModifiedBy
    protected String lastModifiedBy;
    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;

}
