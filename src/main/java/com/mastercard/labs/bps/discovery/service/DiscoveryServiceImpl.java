package com.mastercard.labs.bps.discovery.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import com.mastercard.labs.bps.discovery.webhook.model.ui.DiscoveryTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class DiscoveryServiceImpl implements DiscoveryService {

    private static final Logger logger = LogManager.getLogger(DiscoveryServiceImpl.class);

    @Autowired
    private BatchFileRepository batchFileRepository;
    @Autowired
    private DiscoveryRepository discoveryRepository;
    @Autowired
    private PooledPBEByteEncryptor byteEncryptor;
    @Value("${discovery.delimiter}")
    private char delimiter;


    @Override
    public BatchFile store(@NotNull MultipartFile file, BatchFile.TYPE type, BatchFile.ENTITY entityType) throws IOException {
        return getBatchFile(file, type, entityType);
    }

    private BatchFile getBatchFile(@NotNull MultipartFile file, BatchFile.TYPE type, BatchFile.ENTITY entityType) throws IOException {
        if (FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("csv")) {
            BatchFile batchFile = new BatchFile();
            batchFile.setFileName(getFileName(file.getOriginalFilename()));
            batchFile.setContent(byteEncryptor.encrypt(file.getBytes()));
            batchFile.setEntityType(entityType);
            batchFile.setType(type);
            batchFile.setStatus(BatchFile.STATUS.RECEIVED);
            return batchFileRepository.save(batchFile);
        } else
            return null;
    }

    public enum VALIDATION {
        ZIP(DiscoveryConst.ZIP),
        COUNTRY(DiscoveryConst.COUNTRY),
        STATE(DiscoveryConst.STATE_PROVINCE),
        ADDRESS_1(DiscoveryConst.ADDRESS_LINE_1),
        COMPANY_NAME(DiscoveryConst.COMPANY_NAME);

        private String value;

        VALIDATION(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public List<DiscoveryTable> getBatches(Integer timeZone) {
        return batchFileRepository.findAll().stream().sorted(Comparator.comparing(BatchFile::getCreationDate).reversed()).map(batchFile -> new DiscoveryTable(batchFile.getId(), batchFile.getFileName(), batchFile.getCreationDate(), timeZone, batchFile.getStatus(), batchFile.getType(), batchFile.getEntityType())).collect(Collectors.toList());
    }

    @Override
    public Set<VALIDATION> isBatchValid(BatchFile batchFile) {
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build().withColumnSeparator(delimiter);
        Set<VALIDATION> validations = new HashSet<>();
        if (batchFile != null) {
            CsvMapper csvMapper = new CsvMapper();
            try {
                csvMapper.readerFor(Map.class).with(csvSchema).readValues(byteEncryptor.decrypt(batchFile.getContent())).readAll().stream().filter(o -> o instanceof LinkedHashMap).map(o -> (LinkedHashMap<String, String>) o).forEach(map -> {
                    validations.addAll(Stream.of(VALIDATION.values()).filter(v -> StringUtils.isEmpty(map.get(v.getValue()))).collect(Collectors.toSet()));
                });
                return validations;
            } catch (IOException e) {
                log.error(e.getMessage(), e.getLocalizedMessage(), e);
            }
        }
        return validations;
    }


    public String getFileName(String fileName) {
        return Stream.of(RandomStringUtils.randomAlphanumeric(7), fileName).collect(Collectors.joining("."));
    }

    public BatchFile isBatchFileReady(String id) {
        return batchFileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("File with id " + id + " not found"));
    }

    public List<Discovery> getDiscoveries(String batchId) {
        return discoveryRepository.findByBatchId(batchId);
    }
}
