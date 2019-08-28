package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.webhook.model.ui.DiscoveryTable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface DiscoveryService {

    BatchFile store(MultipartFile file, BatchFile.TYPE lookup, BatchFile.ENTITY supplier) throws IOException;
    Set<DiscoveryServiceImpl.VALIDATION> isBatchValid(BatchFile batchFile);
    public List<DiscoveryTable> getBatches(Integer timeZone);
}