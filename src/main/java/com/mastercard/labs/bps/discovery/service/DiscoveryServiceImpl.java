package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.domain.journal.Record;
import com.mastercard.labs.bps.discovery.domain.journal.Registration;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RegistrationRepository;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import com.mastercard.labs.bps.discovery.webhook.model.ui.DiscoveryTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
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
    private RegistrationRepository registrationRepository;
    @Autowired
    private PooledPBEByteEncryptor byteEncryptor;
    @Value("${discovery.delimiter}")
    private char delimiter;


    @Override
    public BatchFile store(@NotNull MultipartFile file, BatchFile.TYPE type, BatchFile.ENTITY entityType) throws IOException {
        return store(file, type, entityType, null);
    }

    public BatchFile store(MultipartFile file, BatchFile.TYPE type, BatchFile.ENTITY entityType, String agentName) throws IOException {
        return getBatchFile(file, type, entityType, agentName);
    }


    private BatchFile getBatchFile(@NotNull MultipartFile file, BatchFile.TYPE type, BatchFile.ENTITY entityType, String agentName) throws IOException {
        if (FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("csv")) {
            BatchFile batchFile = new BatchFile();
            batchFile.setFileName(getFileName(file.getOriginalFilename()));
            batchFile.setContent(byteEncryptor.encrypt(file.getBytes()));
            batchFile.setEntityType(entityType);
            batchFile.setType(type);
            batchFile.setStatus(BatchFile.STATUS.RECEIVED);
            batchFile.setAgentName(agentName);
            return batchFileRepository.save(batchFile);
        } else
            return null;
    }

    public enum VALIDATION {
        ZIP(DiscoveryConst.ZIP, "^[0-9]{5}$"),
        COUNTRY(DiscoveryConst.COUNTRY, "^[a-zA-Z]{2}|[a-zA-Z]{3}$"),
        STATE(DiscoveryConst.STATE_PROVINCE, "^[a-zA-Z]{2}$"),
        ADDRESS_1(DiscoveryConst.ADDRESS_LINE_1, "([0-9a-zA-Z _\\-\\.,]+)"),
        COMPANY_NAME(DiscoveryConst.COMPANY_NAME, "^(\\S)+.(\\S)+@bps$"),
        CITY(DiscoveryConst.CITY, "([a-zA-Z -\\.]+)");

        private String value;
        private String regex;

        VALIDATION(String value, String regex) {
            this.value = value;
            this.regex = regex;
        }

        public String getValue() {
            return value;
        }

        public static boolean validate(Record record, BatchFile.ENTITY entity) {
            if (record != null) {
                return Pattern.compile(ADDRESS_1.regex).matcher(Optional.ofNullable(record.getAddress1()).orElse("")).matches() &
                        Pattern.compile(ZIP.regex).matcher(Optional.ofNullable(record.getZip()).orElse("")).matches() &
                        Pattern.compile(COUNTRY.regex).matcher(Optional.ofNullable(record.getCountry()).orElse("")).matches() &
                        StringUtils.equalsAnyIgnoreCase(record.getCountry(), "US", "USA") ? Pattern.compile(STATE.regex).matcher(Optional.ofNullable(record.getState()).orElse("")).matches() : StringUtils.isBlank(record.getState()) &
                        entity == BatchFile.ENTITY.BUYER ? Pattern.compile(COMPANY_NAME.regex).matcher(Optional.ofNullable(record.getCompanyName()).orElse("")).matches() : true &
                        Pattern.compile(CITY.regex).matcher(Optional.ofNullable(record.getCity()).orElse("")).matches();

            }
            return true;
        }

    }

    public List<DiscoveryTable> getBatches(Integer timeZone) {
        return batchFileRepository.findAll().stream().sorted(Comparator.comparing(BatchFile::getCreationDate).reversed()).map(batchFile -> new DiscoveryTable(batchFile.getId(), batchFile.getFileName(), batchFile.getCreationDate(), timeZone, batchFile.getStatus(), batchFile.getType(), batchFile.getEntityType())).collect(Collectors.toList());
    }

    @Override
    public boolean isDiscoveryValid(Record record, BatchFile.ENTITY entity) {
        return VALIDATION.validate(record, entity);
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

    public List<Registration> getRegistrations(String batchId) {
        return registrationRepository.findByBatchId(batchId);
    }
}
