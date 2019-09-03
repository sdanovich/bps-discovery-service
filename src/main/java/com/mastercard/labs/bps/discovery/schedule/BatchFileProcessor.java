package com.mastercard.labs.bps.discovery.schedule;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery.STATUS;
import com.mastercard.labs.bps.discovery.domain.journal.Record;
import com.mastercard.labs.bps.discovery.domain.journal.Registration;
import com.mastercard.labs.bps.discovery.exceptions.ExecutionException;
import com.mastercard.labs.bps.discovery.exceptions.ValidationException;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RegistrationRepository;
import com.mastercard.labs.bps.discovery.service.DiscoveryServiceImpl;
import com.mastercard.labs.bps.discovery.service.RestTemplateServiceImpl;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import com.mastercard.labs.bps.discovery.webhook.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

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
    private RestTemplateServiceImpl restTemplateService;

    @Autowired
    private DiscoveryServiceImpl discoveryService;

    @Value("${discovery.delimiter}")
    private char delimiter;

    @Value("${directory.supplierByTrackId}")
    private String pathToSupplier;

    @Value("${directory.buyerByTrackId}")
    private String pathToBuyer;

    private static final String INCONCLUSIVE_STR = "Inconclusive";

    @Scheduled(fixedRate = 1000 * 20)
    public void process() {
        batchFileRepository.findByStatus(RECEIVED).orElse(Collections.emptySet()).parallelStream().filter(Objects::nonNull).forEach(batchFile -> {
            try {
                batchFile.setStatus(PROCESSING);
                processIncoming(batchFileRepository.save(batchFile));

            } catch (Exception e) {
                log.info("cannot parse", e.getLocalizedMessage(), e);
            }
        });
    }

    @Scheduled(fixedRate = 1000 * 20)
    public void updateStatus() {
        batchFileRepository.findAllProcessedDiscoveryBatches().stream().filter(Objects::nonNull).forEach(batchFile -> {
            try {
                batchFileRepository.save(batchFileRepository.getOne(batchFile.getId()).withStatus(COMPLETE));
            } catch (Exception e) {
                log.info("cannot update status", e.getLocalizedMessage(), e);
            }
        });
    }

    private void processIncoming(BatchFile batchFile) {
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build().withColumnSeparator(delimiter);

        if (batchFile != null) {

            CsvMapper csvMapper = new CsvMapper();
            try {
                //TODO: error - cannot be multiple
                csvMapper.readerFor(Map.class).with(csvSchema).readValues(byteEncryptor.decrypt(batchFile.getContent())).readAll().parallelStream().filter(o -> o instanceof LinkedHashMap).map(o -> (LinkedHashMap<String, String>) o).forEach(map -> {
                    if (batchFile.getType() == BatchFile.TYPE.LOOKUP) {
                        persistRecord(batchFile, discoveryRepository.save(getDiscovery(batchFile, map)));
                    } else if (batchFile.getType() == BatchFile.TYPE.REGISTRATION) {
                        Registration registration = (Registration) persistRecord(batchFile, registrationRepository.save(getRegistration(batchFile, map)));
                        register(batchFile, registration);
                    }

                });

            } catch (IOException e) {
                log.error(e.getMessage(), e.getLocalizedMessage(), e);
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
        registration.setEntity(batchFile.getEntityType());
        return registration;
    }

    private Record persistRecord(BatchFile batchFile, Record record) {
        try {
            if (batchFile.getType() == BatchFile.TYPE.REGISTRATION && !discoveryService.isDiscoveryValid(record, batchFile.getEntityType())) {
                record.setReason("Registration Error - Missing required fields");
                throw new ValidationException(record.getReason());
            }
            ResponseEntity<TrackResponseModel> trackResponseModelResponseEntity = restTemplateService.callTrack(record).get();
            if (trackResponseModelResponseEntity.getStatusCode().is2xxSuccessful()) {
                record.setStatus(STATUS.COMPLETE);
                if (!CollectionUtils.isEmpty(trackResponseModelResponseEntity.getBody().getResponseDetail())) {
                    List<TrackResponseModel.ResponseDetail> responseDetails = trackResponseModelResponseEntity.getBody().getResponseDetail();
                    if (responseDetails.size() > 1) {
                        //TODO: error - cannot be multiple
                        record.setFound(Discovery.EXISTS.N);
                        record.setReason("ERROR: " + "Multiple results");
                    } else if (responseDetails.get(0).getMatchResults() != null && responseDetails.get(0).getMatchResults().getMatchScoreData() != null) {
                        record.setConfidence(responseDetails.get(0).getMatchResults().getMatchStatus());
                        Integer rating = responseDetails.get(0).getMatchResults().getMatchScoreData().getMatchPercentage();
                        if (responseDetails.get(0).getRequestData() != null) {
                            record.setTrackId(responseDetails.get(0).getRequestData().getTrackId());
                        }
                        if (rating != null) {
                            record.setFound((2 == rating) ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                            if (record.getFound() == Discovery.EXISTS.Y) {
                                if (batchFile.getType() == BatchFile.TYPE.LOOKUP) {
                                    if (batchFile.getEntityType() == BatchFile.ENTITY.BUYER) {
                                        ((Discovery) record).setBpsPresent(restTemplateService.getCompanyFromDirectory(StringUtils.replace(pathToBuyer, "{trackid}", responseDetails.get(0).getRequestData().getTrackId()), BuyerAgent.class).isPresent() ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                                    } else if (batchFile.getEntityType() == BatchFile.ENTITY.SUPPLIER) {
                                        ((Discovery) record).setBpsPresent(restTemplateService.getCompanyFromDirectory(StringUtils.replace(pathToSupplier, "{trackid}", responseDetails.get(0).getRequestData().getTrackId()), SupplierAgent.class).isPresent() ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                                    }
                                }
                            }
                        } else {
                            record.setFound(Discovery.EXISTS.N);
                        }
                    } else {
                        record.setFound(Discovery.EXISTS.N);
                        record.setReason(INCONCLUSIVE_STR);
                        log.info("ERROR: Empty result");
                    }
                }

            } else {
                record.setFound(Discovery.EXISTS.I);
                record.setStatus(STATUS.FAILED);
                record.setReason(INCONCLUSIVE_STR);
                log.info("ERROR: " + trackResponseModelResponseEntity.getStatusCodeValue());
            }

        } catch (Exception e) {
            record.setFound(Discovery.EXISTS.I);
            record.setStatus(STATUS.FAILED);
            record.setReason(INCONCLUSIVE_STR);
            log.error(e.getMessage(), e.getLocalizedMessage(), e);
        }
        return (record instanceof Discovery) ? discoveryRepository.save((Discovery) record) : registrationRepository.save((Registration) record);
    }


    private void register(BatchFile batchFile, Registration registration) throws ExecutionException {
        if (!StringUtils.isBlank(batchFile.getAgentName())) {
            BusinessEntity businessEntity = new BusinessEntity();
            Address address = new Address();
            address.setAddress1(registration.getAddress1());
            address.setAddress2(registration.getAddress2());
            address.setAddress3(registration.getAddress3());
            address.setCity(registration.getCity());
            address.setState(registration.getState());
            address.setCountry(registration.getCountry());
            address.setZip(registration.getZip());
            businessEntity.setAddress(address);
            businessEntity.setName(registration.getCompanyName());


            //, String bpsId, String agentName
        } else {
            throw new com.mastercard.labs.bps.discovery.exceptions.ExecutionException("Error: Agent name doesn't exist");
        }
    }


}