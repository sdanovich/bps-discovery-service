package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.*;
import com.mastercard.labs.bps.discovery.exceptions.*;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RegistrationRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RulesRegistrationRepository;
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
import org.springframework.http.ResponseEntity;

import javax.net.ssl.SSLException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
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

    @Value("${rules.rejectMsg}")
    private String rejectMsg;

    @Value("${rules.warningMsg}")
    private String warningMsg;

    @Autowired
    private RulesRegistrationRepository rulesRepository;

    private static final String INCONCLUSIVE_STR = "Inconclusive";
    private static final String AMOUNT_RULE_NAME = "Invoice Amount control";

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

    public enum RULESVALIDATION {

        SUPPLIER_ID(DiscoveryConst.SUPPLIER_ID, "^(\\s)$");

        private String value;
        private String regex;

        RULESVALIDATION(String value, String regex) {
            this.value = value;
            this.regex = regex;
        }

        public static boolean validate(Rules record, BatchFile.ENTITY entity) {
            boolean valid = true;
            if (record != null) {
                Pattern.compile(SUPPLIER_ID.regex).matcher(Optional.ofNullable(record.getSupplierId()).orElse("")).matches();
                if (valid ) {
                    valid &= StringUtils.isNotBlank(record.getEnforcementType());
                }
            }
            return valid;
        }
    }



    public boolean isRulesValid(Rules record, BatchFile.ENTITY entity) {
        return RULESVALIDATION.validate(record, entity);
    }


    public List<Rules> getRules(String batchId) {
        return rulesRepository.findByBatchId(batchId);
    }



    public Rules persistRules(String id) {
        logger.debug("Rule Primary Key ID: "+id);
        logger.debug("Rule Processed Primary Key ID: "+id);

        Rules rules = rulesRepository.findById(id).orElseThrow(() -> new ExecutionException("Rules " + id + " not found"));
        BatchFile batchFile = batchFileRepository.findById(rules.getBatchId()).orElseThrow(() -> new ExecutionException("Batch File " + id + " not found"));
        return persistRules(batchFile, rules);
    }




    public Rules persistRules(BatchFile batchFile, Rules record) {
        ResponseEntity<RuleResponse> ruleResponseResponseEntity = null;
        try {
            if (batchFile.getType() == BatchFile.TYPE.RULES && !isRulesValid(record, batchFile.getEntityType())) {
                record.setReason("Rules Registration Error - Missing required fields");
                record.setStatus(Discovery.STATUS.FAILED);
                throw new ValidationException(record.getReason());
            }
            RuleRequestModel ruleRequestModel = new RuleRequestModel();
            ruleRequestModel.setAmount(record.getMaxAmtLimit());
            ruleRequestModel.setAmountOperator(RuleRequestModel.AmountOperatorEnum.OVER);
            ruleRequestModel.setBuyerTaxIds(getBuyerTaxIdList(record));
            ruleRequestModel.setName(AMOUNT_RULE_NAME);
            ruleRequestModel.setSupplierProfileId(record.getSupplierId());
            ruleRequestModel.setDecisions(getDecisionPathList(record));
            ruleResponseResponseEntity = restTemplateService.callRulesEngine(ruleRequestModel).get();
            if (ruleResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                record.setStatus(Discovery.STATUS.COMPLETE);
                if (!StringUtils.isEmpty(getRulesResponseDetails(ruleResponseResponseEntity).getDescription())) {
                    RuleResponse responseDetails = getRulesResponseDetails(ruleResponseResponseEntity);
                    record.setReason(responseDetails.getDescription());
                }
            } else {
                record.setReason("Create Rule Request Failed");
                record.setStatus(Discovery.STATUS.FAILED);
                log.info("ERROR: " + ruleResponseResponseEntity.getStatusCodeValue());
            }

        } catch (Exception e) {
            record.setStatus(Discovery.STATUS.FAILED);
            if (StringUtils.isBlank(record.getReason())) record.setReason(INCONCLUSIVE_STR);
            log.error(e.getMessage(), e.getLocalizedMessage(), e);
        }
        return  rulesRepository.save(record);
    }

    private RuleResponse getRulesResponseDetails(ResponseEntity<RuleResponse> ruleResponseEntity) {
        return Optional.ofNullable(ruleResponseEntity.getBody()).orElse(new RuleResponse());
    }





    private List<BuyerTaxId> getBuyerTaxIdList(Rules record) {
        List<BuyerTaxId> taxIdList = new ArrayList<>();
        BuyerTaxId.OperationEnum operationEnum = getBuyerTaxIdOperation(record);
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId1())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId1());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }

        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId2())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId2());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }

        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId3())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId3());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }

        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId4())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId4());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId5())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId5());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId6())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId6());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId7())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId7());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId8())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId8());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId9())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId9());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId10())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId10());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId11())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId11());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId12())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId12());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId13())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId13());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId14())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId14());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId15())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId15());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId16())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId16());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId17())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId17());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId18())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId18());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId19())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId19());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        if(!org.apache.commons.lang.StringUtils.isBlank(record.getBuyerTaxId20())) {
            BuyerTaxId buyerTaxId = new BuyerTaxId();
            buyerTaxId.setBuyerTaxId(record.getBuyerTaxId20());
            buyerTaxId.setOperation(operationEnum);
            taxIdList.add(buyerTaxId);
        }
        return taxIdList;
    }

    private BuyerTaxId.OperationEnum getBuyerTaxIdOperation(Rules record)
    {
        return record.getRelationship().equalsIgnoreCase("I")?BuyerTaxId.OperationEnum.INCLUDED:BuyerTaxId.OperationEnum.EXCLUDED;
    }

    private List<DecisionPath> getDecisionPathList(Rules record) {
        List<DecisionPath> decisionPathList = new ArrayList<>();
        DecisionPath decisionPath = new DecisionPath();
        decisionPath.setDecisionCode(record.getEnforcementType().equalsIgnoreCase("W")?DecisionPath.DecisionCodeEnum.WARNING:DecisionPath.DecisionCodeEnum.REJECT);
        decisionPath.setDecisionDescription(record.getEnforcementType().equalsIgnoreCase("W")?warningMsg:rejectMsg);
        decisionPathList.add(decisionPath);
        return decisionPathList;
    }

}
