package com.mastercard.labs.bps.discovery.schedule;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery.STATUS;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.service.RestTemplateServiceImpl;
import com.mastercard.labs.bps.discovery.util.DiscoveryConst;
import com.mastercard.labs.bps.discovery.webhook.model.BuyerAgent;
import com.mastercard.labs.bps.discovery.webhook.model.SupplierAgent;
import com.mastercard.labs.bps.discovery.webhook.model.TrackResponseModel;
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
    private PooledPBEByteEncryptor byteEncryptor;

    @Autowired
    private RestTemplateServiceImpl restTemplateService;

    @Value("${discovery.delimiter}")
    private char delimiter;

    @Value("${directory.supplierByTrackId}")
    private String pathToSupplier;

    @Value("${directory.buyerByTrackId}")
    private String pathToBuyer;


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
                    Discovery discovery = new Discovery();
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
                    try {
                        discovery = discoveryRepository.save(discovery);
                        ResponseEntity<TrackResponseModel> trackResponseModelResponseEntity = restTemplateService.callTrack(discovery).get();
                        if (trackResponseModelResponseEntity.getStatusCode().is2xxSuccessful()) {
                            discovery.setStatus(STATUS.COMPLETE);
                            if (!CollectionUtils.isEmpty(trackResponseModelResponseEntity.getBody().getResponseDetail())) {
                                List<TrackResponseModel.ResponseDetail> responseDetails = trackResponseModelResponseEntity.getBody().getResponseDetail();
                                if (responseDetails.size() > 1) {
                                    //TODO: error - cannot be multiple
                                    discovery.setFound(Discovery.EXISTS.N);
                                    discovery.setReason("ERROR: " + "Multiple results");
                                } else if (responseDetails.get(0).getMatchResults() != null && responseDetails.get(0).getMatchResults().getMatchScoreData() != null) {
                                    discovery.setConfidence(responseDetails.get(0).getMatchResults().getMatchStatus());
                                    Integer rating = responseDetails.get(0).getMatchResults().getMatchScoreData().getMatchPercentage();
                                    if (responseDetails.get(0).getRequestData() != null) {
                                        discovery.setTrackId(responseDetails.get(0).getRequestData().getTrackId());
                                    }
                                    if (rating != null) {
                                        discovery.setFound((2 == rating) ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                                        if (discovery.getFound() == Discovery.EXISTS.Y) {
                                            if (batchFile.getEntityType() == BatchFile.ENTITY.BUYER) {
                                                discovery.setBpsPresent(restTemplateService.getCompanyFromDirectory(StringUtils.replace(pathToBuyer, "{trackid}", responseDetails.get(0).getRequestData().getTrackId()), BuyerAgent.class).isPresent() ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                                            } else if (batchFile.getEntityType() == BatchFile.ENTITY.SUPPLIER) {
                                                discovery.setBpsPresent(restTemplateService.getCompanyFromDirectory(StringUtils.replace(pathToSupplier,"{trackid}", responseDetails.get(0).getRequestData().getTrackId()), SupplierAgent.class).isPresent() ? Discovery.EXISTS.Y : Discovery.EXISTS.N);
                                            }
                                        }
                                    } else {
                                        discovery.setFound(Discovery.EXISTS.N);
                                    }
                                } else {
                                    discovery.setFound(Discovery.EXISTS.N);
                                    discovery.setReason("ERROR: " + "Empty result");
                                }
                            }

                        } else {
                            discovery.setFound(Discovery.EXISTS.I);
                            discovery.setStatus(STATUS.FAILED);
                            discovery.setReason("ERROR: " + trackResponseModelResponseEntity.getStatusCodeValue());
                        }

                    } catch (Exception e) {
                        discovery.setFound(Discovery.EXISTS.I);
                        discovery.setStatus(STATUS.FAILED);
                        discovery.setReason(e.getMessage());
                    }
                    discoveryRepository.save(discovery);
                });

            } catch (IOException e) {
                log.error(e.getMessage(), e.getLocalizedMessage(), e);
            }
        }
    }
}
