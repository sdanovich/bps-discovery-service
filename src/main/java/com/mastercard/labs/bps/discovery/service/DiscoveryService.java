package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Record;
import com.mastercard.labs.bps.discovery.webhook.model.ui.DiscoveryTable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface DiscoveryService {

    BatchFile store(MultipartFile file, BatchFile.TYPE lookup, BatchFile.ENTITY supplier) throws IOException;

    boolean isDiscoveryValid(Record record, BatchFile.ENTITY entity);

    List<DiscoveryTable> getBatches(Integer timeZone);

    Record persistDiscovery(String id);

    Record persistRegistration(String id);

    Record persistRecord(BatchFile batchFile, Record record);

}