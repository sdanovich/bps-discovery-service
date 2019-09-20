package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.domain.journal.Record;
import com.mastercard.labs.bps.discovery.domain.journal.Registration;
import com.mastercard.labs.bps.discovery.exceptions.*;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RegistrationRepository;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import com.mastercard.labs.bps.discovery.webhook.model.*;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
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
    private EventService eventService;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private RestTemplateServiceImpl restTemplateService;
    @Autowired
    private PooledPBEByteEncryptor byteEncryptor;
    @Value("${discovery.delimiter}")
    private char delimiter;
    @Autowired
    private Environment environment;
    @Value("${directory.supplierByTrackId}")
    private String pathToSupplier;

    @Value("${directory.buyerByTrackId}")
    private String pathToBuyer;

    private static final String INCONCLUSIVE_STR = "Inconclusive";


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
        COMPANY_NAME(DiscoveryConst.COMPANY_NAME, "^(\\s)$"),
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
            boolean valid = true;
            if (record != null) {
                valid &= Pattern.compile(ADDRESS_1.regex).matcher(Optional.ofNullable(record.getAddress1()).orElse("")).matches() &
                        Pattern.compile(ZIP.regex).matcher(Optional.ofNullable(record.getZip()).orElse("")).matches() &
                        Pattern.compile(COUNTRY.regex).matcher(Optional.ofNullable(record.getCountry()).orElse("")).matches() &
                        StringUtils.equalsAnyIgnoreCase(record.getCountry(), "US", "USA") ? Pattern.compile(STATE.regex).matcher(Optional.ofNullable(record.getState()).orElse("")).matches() : StringUtils.isBlank(record.getState()) &
                        Pattern.compile(COMPANY_NAME.regex).matcher(Optional.ofNullable(record.getCompanyName()).orElse("")).matches() &
                        Pattern.compile(CITY.regex).matcher(Optional.ofNullable(record.getCity()).orElse("")).matches();
                if (valid && record instanceof Registration) {
                    valid &= StringUtils.isNotBlank(((Registration) record).getBpsId());
                }
            }
            return valid;
        }
    }

    public List<DiscoveryTable> getBatches(Integer timeZone) {
        return batchFileRepository.findAll().stream().sorted(Comparator.comparing(BatchFile::getCreationDate).reversed()).map(batchFile -> {
            int percentage = 0;
            if (batchFile.getStatus() == BatchFile.STATUS.PROCESSING) {
                long total = 0;
                long completed = 0;
                if (batchFile.getType() == BatchFile.TYPE.REGISTRATION) {
                    total = registrationRepository.countByBatchId(batchFile.getId());
                    completed = registrationRepository.countByBatchIdAndStatusIn(batchFile.getId(), Stream.of(Discovery.STATUS.COMPLETE, Discovery.STATUS.FAILED).collect(Collectors.toList()));
                } else if (batchFile.getType() == BatchFile.TYPE.LOOKUP) {
                    total = discoveryRepository.countByBatchId(batchFile.getId());
                    completed = discoveryRepository.countByBatchIdAndStatusIn(batchFile.getId(), Stream.of(Discovery.STATUS.COMPLETE, Discovery.STATUS.FAILED).collect(Collectors.toList()));
                }
                if (total != 0 && completed != 0) {
                    percentage = new Double(completed * 100 / total).intValue();
                    if (percentage < 0) percentage = 0;
                    if (percentage > 100) percentage = 100;
                }
            }
            return new DiscoveryTable(batchFile.getId(), batchFile.getFileName(), batchFile.getCreationDate(), timeZone, batchFile.getStatus(), batchFile.getType(), batchFile.getEntityType(), percentage);
        }).collect(Collectors.toList());
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

    public List<String> getAgents() {
        return restTemplateService.getAgents();
    }

    public Record persistDiscovery(String id) {
        Discovery discovery = discoveryRepository.findById(id).orElseThrow(() -> new ExecutionException("Discovery " + id + " not found"));
        BatchFile batchFile = batchFileRepository.findById(discovery.getBatchId()).orElseThrow(() -> new ExecutionException("Batch File " + id + " not found"));
        return persistRecord(batchFile, discovery);
    }

    public Record persistRegistration(String id) {
        Registration registration = registrationRepository.findById(id).orElseThrow(() -> new ExecutionException("Registration " + id + " not found"));
        BatchFile batchFile = batchFileRepository.findById(registration.getBatchId()).orElseThrow(() -> new ExecutionException("Batch File " + id + " not found"));
        return persistRecord(batchFile, registration);
    }

    public Record persistRecord(BatchFile batchFile, Record record) {
        TrackResponseModel trackResponseModel = null;
        Double rating = 0.0;
        try {
            if (batchFile.getType() == BatchFile.TYPE.REGISTRATION && !isDiscoveryValid(record, batchFile.getEntityType())) {
                record.setReason("Registration Error - Missing required fields");
                record.setStatus(Discovery.STATUS.FAILED);
                throw new ValidationException(record.getReason());
            }
            try {
                trackResponseModel = restTemplateService.postingWithWebClient(record);
            } catch (TrackAccess4xxException | TimeoutException e) {
                eventService.sendDiscovery(batchFile.getId() + "|" + batchFile.getType().name(), record.getId() + "|" + batchFile.getType().name());
                trackResponseModel = null;
            } catch (TrackAccess3xxException e) {
                log.error("Track: The request was redirected ", e);
                logError(record, e);
            } catch (TrackAccess5xxException e) {
                log.error("Track: Server error ", e);
                logError(record, e);
            }

            if (trackResponseModel != null) {
                record.setStatus(Discovery.STATUS.COMPLETE);
                if (!CollectionUtils.isEmpty(getResponseDetails(trackResponseModel))) {
                    List<TrackResponseModel.ResponseDetail> responseDetails = getResponseDetails(trackResponseModel);
                    if (responseDetails.size() > 1) {
                        //TODO: error - cannot be multiple
                        record.setFound(Discovery.EXISTS.N);
                        record.setReason("ERROR: " + "Multiple results");
                    } else if (responseDetails.get(0).getMatchResults() != null && responseDetails.get(0).getMatchResults().getMatchScoreData() != null) {
                        record.setConfidence(responseDetails.get(0).getMatchResults().getMatchStatus());
                        rating = responseDetails.get(0).getMatchResults().getMatchScoreData().getMatchPercentage();
                        if (!CollectionUtils.isEmpty(responseDetails.get(0).getMatchData())) {
                            record.setTrackId(getRegisteredBusinessData(responseDetails).getTrackId());
                        }
                        if (rating != null) {
                            if (batchFile.getType() == BatchFile.TYPE.REGISTRATION && StringUtils.equalsIgnoreCase(record.getConfidence(), "HIGHCONFIDENCE") && rating != 2) {
                                record.setConfidence("PARTIALCONFIDENCE");
                            }
                            record.setFound((2 == rating) ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                            //if (record.getFound() == Discovery.EXISTS.Y) {
                            if (batchFile.getType() == BatchFile.TYPE.LOOKUP) {
                                if (batchFile.getEntityType() == BatchFile.ENTITY.BUYER) {
                                    ((Discovery) record).setBpsPresent(isBpsPresent((Discovery) record, pathToBuyer, BuyerAgent.class) ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                                } else if (batchFile.getEntityType() == BatchFile.ENTITY.SUPPLIER) {
                                    ((Discovery) record).setBpsPresent(isBpsPresent((Discovery) record, pathToSupplier, Supplier.class) ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                                }
                                //}
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
            }
        } catch (Exception e) {
            if (e.getCause() instanceof TimeoutException || e.getCause() instanceof SSLException) {
                eventService.sendDiscovery(batchFile.getId() + "|" + batchFile.getType().name(), record.getId() + "|" + batchFile.getType().name());
            } else {
                record.setFound(Discovery.EXISTS.I);
                record.setStatus(Discovery.STATUS.FAILED);
                if (StringUtils.isBlank(record.getReason())) record.setReason(INCONCLUSIVE_STR);
            }
            log.error(e.getMessage(), e.getLocalizedMessage(), e);
        }
        return (record instanceof Discovery) ? discoveryRepository.save((Discovery) enrich(record, trackResponseModel, rating)) : registrationRepository.save((Registration) enrich(register(batchFile, (Registration) record), trackResponseModel, rating));
    }

    private void logError(Record record, Throwable e) {
        record.setFound(Discovery.EXISTS.I);
        record.setStatus(Discovery.STATUS.FAILED);
        record.setReason(e.getMessage());
    }

    private List<TrackResponseModel.ResponseDetail> getResponseDetails(TrackResponseModel trackResponseModel) {
        return Optional.ofNullable(trackResponseModel).orElse(new TrackResponseModel()).getResponseDetail();
    }

    private <T extends Record> Record enrich(Record record, TrackResponseModel trackResponseModel, Double rating) {
        if (Stream.of(environment.getActiveProfiles()).anyMatch(p -> StringUtils.containsAny(p, "debug", "dev"))) {
            TrackResponseModel.RegisteredBusinessData registeredBusinessData = getRegisteredBusinessData(getResponseDetails(trackResponseModel));
            if (registeredBusinessData != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("score=" + rating + ",");
                sb.append("trackId=" + registeredBusinessData.getTrackId() + ",");
                sb.append("trackName=" + registeredBusinessData.getBusinessName() + ",");
                sb.append("trackAddress=" + registeredBusinessData.getAddress().getStreetAddress() + ",");
                sb.append("trackCity=" + registeredBusinessData.getAddress().getState() + ",");
                sb.append("trackState=" + registeredBusinessData.getAddress().getState() + ",");
                sb.append("trackZip=" + registeredBusinessData.getAddress().getZip());
                //record.setConfidence(record.getConfidence() + " " + sb.toString());

                logger.debug("Track Info ::: " + sb.toString());

                record.setScore("" + rating);
                record.setTrackId(registeredBusinessData.getTrackId());
                record.setBusinessName(registeredBusinessData.getBusinessName());
                record.setStreetAddress(registeredBusinessData.getAddress().getStreetAddress());
                record.setTrackCity(registeredBusinessData.getAddress().getCity());
                record.setTrackState(registeredBusinessData.getAddress().getState());
                record.setTrackZip(registeredBusinessData.getAddress().getZip());
            }
        }
        return record;
    }

    private TrackResponseModel.RegisteredBusinessData getRegisteredBusinessData(List<TrackResponseModel.ResponseDetail> responseDetails) {
        return CollectionUtils.isEmpty(responseDetails) ? null : CollectionUtils.isEmpty(responseDetails.get(0).getMatchData()) ? null : responseDetails.get(0).getMatchData().get(0).getRegisteredBusinessData();
    }

    private <T> boolean isBpsPresent(Discovery discovery, String path, Class<T> clazz) {
        return discovery.getTrackId() != null && !restTemplateService.getCompanyFromDirectory(StringUtils.replace(path, "{trackid}", StringUtils.trim(discovery.getTrackId())), clazz).orElse(Collections.emptyList()).isEmpty();
    }


    private Registration register(BatchFile batchFile, Registration registration) throws ExecutionException {
        if (StringUtils.equalsAnyIgnoreCase(registration.getConfidence(), "HIGHCONFIDENCE") && registration.getStatus() == Discovery.STATUS.COMPLETE) {
            BusinessEntity businessEntity = new BusinessEntity();
            Address address = new Address();
            address.setStreet(registration.getAddress1());
            address.setAddress2(registration.getAddress2());
            address.setAddress3(registration.getAddress3());
            address.setCity(registration.getCity());
            address.setState(registration.getState());
            address.setCountry(registration.getCountry());
            address.setZip(registration.getZip());
            businessEntity.setAddress(address);
            businessEntity.setName(registration.getCompanyName());
            businessEntity.setTaxId(registration.getTaxId());
            businessEntity.setTrackId(registration.getTrackId());
            try {
                switch (batchFile.getEntityType()) {
                    case BUYER:
                        restTemplateService.registerBuyer(businessEntity, registration.getBpsId(), batchFile.getAgentName());
                        break;
                    case SUPPLIER:
                        restTemplateService.registerSupplier(businessEntity, registration.getBpsId(), batchFile.getAgentName());
                        break;
                }
                registration.setStatus(Discovery.STATUS.COMPLETE);
            } catch (ExecutionException e) {
                registration.setReason(e.getMessage());
                registration.setStatus(Discovery.STATUS.FAILED);
            }
        } else {
            if (StringUtils.isBlank(registration.getReason())) registration.setReason(registration.getConfidence());
            registration.setStatus(Discovery.STATUS.FAILED);
        }
        return registration;
    }
}
