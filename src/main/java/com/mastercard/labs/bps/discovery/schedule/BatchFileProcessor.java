package com.mastercard.labs.bps.discovery.schedule;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery.STATUS;
import com.mastercard.labs.bps.discovery.domain.journal.Registration;
import com.mastercard.labs.bps.discovery.domain.journal.Rules;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RegistrationRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RulesRegistrationRepository;
import com.mastercard.labs.bps.discovery.service.EventService;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.mastercard.labs.bps.discovery.domain.journal.BatchFile.STATUS.*;

@Component
@Slf4j
public class BatchFileProcessor {


    @Autowired
    private BatchFileRepository batchFileRepository;

    @Autowired
    private DiscoveryRepository discoveryRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private PooledPBEByteEncryptor byteEncryptor;

    @Autowired
    private EventService eventService;

    @Value("${discovery.delimiter}")
    private char delimiter;

    @Autowired
    private RulesRegistrationRepository rulesRegistrationRepository;

    @Scheduled(fixedRate = 1000 * 2)
    public void process() {
        batchFileRepository.findByStatus(RECEIVED).orElse(Collections.emptySet()).parallelStream().filter(Objects::nonNull).forEach(batchFile -> {
            try {
                batchFile.setStatus(PROCESSING);
                batchFileRepository.save(batchFile);
                String queueName = batchFile.getId() + "|" + batchFile.getType().name();
                eventService.setUpQueue(queueName);
                processIncoming(queueName, batchFile);

            } catch (Exception e) {
                log.info("cannot parse", e.getLocalizedMessage(), e);
            }
        });
    }

    @Scheduled(fixedRate = 1000 * 2)
    public void updateStatus() {
        batchFileRepository.findAllProcessedDiscoveryBatches().stream().filter(Objects::nonNull).forEach(batchFile -> {
            try {
                batchFileRepository.save(batchFileRepository.getOne(batchFile.getId()).withStatus(COMPLETE));
            } catch (Exception e) {
                log.info("cannot update status", e.getLocalizedMessage(), e);
            }
        });
        batchFileRepository.findAllProcessedRegistriesBatches().stream().filter(Objects::nonNull).forEach(batchFile -> {
            try {
                batchFileRepository.save(batchFileRepository.getOne(batchFile.getId()).withStatus(COMPLETE));
            } catch (Exception e) {
                log.info("cannot update status", e.getLocalizedMessage(), e);
            }
        });
        batchFileRepository.findAllProcessedRulesRegistriesBatches().stream().filter(Objects::nonNull).forEach(batchFile -> {
            try {
                batchFileRepository.save(batchFileRepository.getOne(batchFile.getId()).withStatus(COMPLETE));
            } catch (Exception e) {
                log.info("cannot update status", e.getLocalizedMessage(), e);
            }
        });
    }

    private void processIncoming(String queueName, BatchFile batchFile) {
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build().withColumnSeparator(delimiter);

        if (batchFile != null) {

            CsvMapper csvMapper = new CsvMapper();
            try {
                csvMapper.readerFor(Map.class).with(csvSchema).readValues(byteEncryptor.decrypt(batchFile.getContent())).readAll().stream().filter(o -> o instanceof LinkedHashMap).map(o -> (LinkedHashMap<String, String>) o).forEach(map -> {
                    if (batchFile.getType() == BatchFile.TYPE.LOOKUP) {
                        eventService.sendDiscovery(queueName, discoveryRepository.save(getDiscovery(batchFile, map)).getId() + "|" + BatchFile.TYPE.LOOKUP.name());
                    } else if (batchFile.getType() == BatchFile.TYPE.REGISTRATION) {
                        eventService.sendDiscovery(queueName, registrationRepository.save(getRegistration(batchFile, map)).getId() + "|" + BatchFile.TYPE.REGISTRATION.name());
                    } 	else if (batchFile.getType() == BatchFile.TYPE.RULES) {
                        eventService.sendDiscovery(queueName, rulesRegistrationRepository.save(getRulesRegistration(batchFile, map)).getId() + "|" + BatchFile.TYPE.RULES.name());
                    }
                });
            } catch (IOException e) {
                log.error(e.getMessage(), e.getLocalizedMessage(), e);
                batchFile.setStatus(BatchFile.STATUS.INVALID_FILE);
                batchFileRepository.save(batchFile);
            }
        }
    }

    private Discovery getDiscovery(BatchFile batchFile, LinkedHashMap<String, String> map) {
        final Discovery discovery = new Discovery();
        discovery.setBatchId(batchFile.getId());
        discovery.setStatus(STATUS.READY);
        discovery.setDbId(map.get(DiscoveryConst.DB_ID));
        discovery.setTaxId(map.get(DiscoveryConst.TAX_ID));
        discovery.setZip(map.get(DiscoveryConst.ZIP));
        discovery.setCountry(map.get(DiscoveryConst.COUNTRY));
        discovery.setState(map.get(DiscoveryConst.STATE_PROVINCE));
        discovery.setCity(map.get(DiscoveryConst.CITY));
        discovery.setAddress3(map.get(DiscoveryConst.ADDRESS_LINE_3));
        discovery.setAddress2(map.get(DiscoveryConst.ADDRESS_LINE_2));
        discovery.setAddress1(map.get(DiscoveryConst.ADDRESS_LINE_1));
        discovery.setCompanyName(map.get(DiscoveryConst.COMPANY_NAME));
        return discovery;
    }

    private Registration getRegistration(BatchFile batchFile, LinkedHashMap<String, String> map) {
        final Registration registration = new Registration();
        registration.setBatchId(batchFile.getId());
        registration.setStatus(STATUS.READY);
        registration.setDbId(map.get(DiscoveryConst.DB_ID));
        registration.setTaxId(map.get(DiscoveryConst.TAX_ID));
        registration.setZip(map.get(DiscoveryConst.ZIP));
        registration.setCountry(map.get(DiscoveryConst.COUNTRY));
        registration.setState(map.get(DiscoveryConst.STATE_PROVINCE));
        registration.setCity(map.get(DiscoveryConst.CITY));
        registration.setAddress3(map.get(DiscoveryConst.ADDRESS_LINE_3));
        registration.setAddress2(map.get(DiscoveryConst.ADDRESS_LINE_2));
        registration.setAddress1(map.get(DiscoveryConst.ADDRESS_LINE_1));
        registration.setCompanyName(map.get(DiscoveryConst.COMPANY_NAME));
        registration.setBpsId(map.get(batchFile.getEntityType() == BatchFile.ENTITY.BUYER ? DiscoveryConst.BUYER_ID : DiscoveryConst.SUPPLIER_ID));
        registration.setEntityType(batchFile.getEntityType());
        return registration;
    }

    private Rules getRulesRegistration(BatchFile batchFile, LinkedHashMap<String, String> map) {
        final Rules rules = new Rules();
        rules.setBatchId(batchFile.getId());
        rules.setSupplierId(map.get(DiscoveryConst.SUPPLIER_ID));
        rules.setEnforcementType(map.get(DiscoveryConst.ENFORCEMENT_TYPE));
        rules.setMaxAmtLimit(Integer.valueOf(map.get(DiscoveryConst.MAX_AMT_LIMIT)));
        rules.setRelationship(map.get(DiscoveryConst.RELATIONSHIP));
        rules.setStatus(STATUS.READY);
        rules.setReason(map.get(DiscoveryConst.REASON));
        rules.setBuyerTaxId1(map.get(DiscoveryConst.BUYER_TAX_ID_1));
        rules.setBuyerTaxId2(map.get(DiscoveryConst.BUYER_TAX_ID_2));
        rules.setBuyerTaxId3(map.get(DiscoveryConst.BUYER_TAX_ID_3));
        rules.setBuyerTaxId4(map.get(DiscoveryConst.BUYER_TAX_ID_4));
        rules.setBuyerTaxId5(map.get(DiscoveryConst.BUYER_TAX_ID_5));
        rules.setBuyerTaxId6(map.get(DiscoveryConst.BUYER_TAX_ID_6));
        rules.setBuyerTaxId7(map.get(DiscoveryConst.BUYER_TAX_ID_7));
        rules.setBuyerTaxId8(map.get(DiscoveryConst.BUYER_TAX_ID_8));
        rules.setBuyerTaxId9(map.get(DiscoveryConst.BUYER_TAX_ID_9));
        rules.setBuyerTaxId10(map.get(DiscoveryConst.BUYER_TAX_ID_10));
        rules.setBuyerTaxId11(map.get(DiscoveryConst.BUYER_TAX_ID_11));
        rules.setBuyerTaxId12(map.get(DiscoveryConst.BUYER_TAX_ID_12));
        rules.setBuyerTaxId13(map.get(DiscoveryConst.BUYER_TAX_ID_13));
        rules.setBuyerTaxId14(map.get(DiscoveryConst.BUYER_TAX_ID_14));
        rules.setBuyerTaxId15(map.get(DiscoveryConst.BUYER_TAX_ID_15));
        rules.setBuyerTaxId16(map.get(DiscoveryConst.BUYER_TAX_ID_16));
        rules.setBuyerTaxId17(map.get(DiscoveryConst.BUYER_TAX_ID_17));
        rules.setBuyerTaxId18(map.get(DiscoveryConst.BUYER_TAX_ID_18));
        rules.setBuyerTaxId19(map.get(DiscoveryConst.BUYER_TAX_ID_19));
        rules.setBuyerTaxId20(map.get(DiscoveryConst.BUYER_TAX_ID_20));
        return rules;
    }

}