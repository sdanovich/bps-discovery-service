package com.mastercard.labs.bps.discovery.persistence.repository;

import com.mastercard.labs.bps.discovery.domain.journal.Rules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RulesRegistrationRepository extends JpaRepository<Rules, String> {

    List<Rules> findByBatchId(String batchId);

}