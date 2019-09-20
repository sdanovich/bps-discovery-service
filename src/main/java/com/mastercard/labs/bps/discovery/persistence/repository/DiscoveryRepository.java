package com.mastercard.labs.bps.discovery.persistence.repository;

import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscoveryRepository extends JpaRepository<Discovery, String> {

    List<Discovery> findByBatchId(String batchId);
    long countByBatchId(String batchId);

    long countByBatchIdAndStatusIn(String batchId, List<Discovery.STATUS> statuses);

}
