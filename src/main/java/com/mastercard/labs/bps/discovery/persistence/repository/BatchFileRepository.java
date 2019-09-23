package com.mastercard.labs.bps.discovery.persistence.repository;

import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BatchFileRepository extends JpaRepository<BatchFile, String> {

    Optional<Set<BatchFile>> findByStatus(BatchFile.STATUS status);

    @Query(value = "select bf from BatchFile bf inner join com.mastercard.labs.bps.discovery.domain.journal.Discovery pm on bf.id = pm.batchId where bf.status in ('PROCESSING') and pm.status in ('COMPLETE', 'FAILED') and bf.id not in (select bf.id from BatchFile bf inner join com.mastercard.labs.bps.discovery.domain.journal.Discovery pm on bf.id = pm.batchId where bf.status in ('PROCESSING') and pm.status in ('READY'))")
    List<BatchFile> findAllProcessedDiscoveryBatches();

    @Query(value = "select bf from BatchFile bf inner join com.mastercard.labs.bps.discovery.domain.journal.Registration pm on bf.id = pm.batchId where bf.status in ('PROCESSING') and pm.status in ('COMPLETE', 'FAILED') and bf.id not in (select bf.id from BatchFile bf inner join com.mastercard.labs.bps.discovery.domain.journal.Registration pm on bf.id = pm.batchId where bf.status in ('PROCESSING') and pm.status in ('READY'))")
    List<BatchFile> findAllProcessedRegistriesBatches();

    @Query(value = "select bf from BatchFile bf inner join com.mastercard.labs.bps.discovery.domain.journal.Rules pm on bf.id = pm.batchId where bf.status in ('PROCESSING') and pm.status in ('COMPLETE', 'FAILED') and bf.id not in (select bf.id from BatchFile bf inner join com.mastercard.labs.bps.discovery.domain.journal.Rules pm on bf.id = pm.batchId where bf.status in ('PROCESSING') and pm.status in ('READY'))")
    List<BatchFile> findAllProcessedRulesRegistriesBatches();
}
