package com.mastercard.labs.bps.discovery.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.service.DiscoveryServiceImpl;
import com.mastercard.labs.bps.discovery.webhook.model.DiscoveryModelFull;
import com.mastercard.labs.bps.discovery.webhook.model.DiscoveryModelPartial;
import com.mastercard.labs.bps.discovery.webhook.model.TrackRequestModel;
import com.mastercard.labs.bps.discovery.webhook.model.TrackResponseModel;
import com.mastercard.labs.bps.discovery.webhook.model.ui.DiscoveryTable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.BoundMapperFacade;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static java.util.Collections.singletonList;

@RestController
@ApiIgnore
@Slf4j
public class DiscoveryController {

    @Autowired
    private DiscoveryServiceImpl discoveryService;
    @Autowired
    private BoundMapperFacade<Discovery, DiscoveryModelFull> discoveryToCSVFull;
    @Autowired
    private BoundMapperFacade<Discovery, DiscoveryModelPartial> discoveryToCSVPartial;


    @PostMapping(value = "/discovery/suppliers", produces = {"application/json"})
    public ResponseEntity<Link> handleSupplierLookup(@RequestParam("file") MultipartFile file) throws IOException {
        return getLinkResponseEntity(discoveryService.store(file, BatchFile.TYPE.LOOKUP, BatchFile.ENTITY.SUPPLIER), "suppliers");
    }

    @PostMapping(value = "/discovery/buyers", produces = {"application/json"})
    public ResponseEntity<Link> handleBuyersLookup(@RequestParam("file") MultipartFile file) throws IOException {
        return getLinkResponseEntity(discoveryService.store(file, BatchFile.TYPE.LOOKUP, BatchFile.ENTITY.BUYER), "buyers");
    }

    @GetMapping(value = "/discovery/suppliers/{id}", produces = {"application/json"})
    public ResponseEntity<?> provideSupplierLookup(@PathVariable("id") String id) {
        return handleFile(id, discoveryToCSVFull, DiscoveryModelFull.class);
    }

    @GetMapping(value = "/discovery/buyers/{id}", produces = {"application/json"})
    public ResponseEntity<?> provideBuyerLookup(@PathVariable("id") String id) {
        return handleFile(id, discoveryToCSVPartial, DiscoveryModelPartial.class);
    }

    private URI getUri(String path, BatchFile batchFile) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/discovery/" + path + "/{id}")
                .buildAndExpand(batchFile.getId()).toUri();
    }

    private ResponseEntity<Link> getLinkResponseEntity(BatchFile batchFile, String entity) throws MalformedURLException {
        Set<DiscoveryServiceImpl.VALIDATION> validations = discoveryService.isBatchValid(batchFile);
        if (!validations.isEmpty()) {
            return new ResponseEntity(new Link(null, validations.stream().map(v -> "Error: " + v.getValue() + " is required").toArray(String[]::new)), HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(new Link(getUri(entity, batchFile).toASCIIString(), null));
        }
    }

    private <T> ResponseEntity handleFile(String id, BoundMapperFacade<Discovery, T> boundMapperFacade, Class<T> model) {
        try {
            BatchFile batchFile = discoveryService.isBatchFileReady(id);
            if (batchFile.getStatus() == BatchFile.STATUS.COMPLETE) {
                CsvMapper mapper = new CsvMapper();
                mapper.disable(SORT_PROPERTIES_ALPHABETICALLY);
                CsvSchema schema = mapper.schemaFor(model).withHeader();
                byte[] isr = mapper.writer(schema)
                        .writeValueAsString(discoveryService.getDiscoveries(batchFile.getId()).stream().map(boundMapperFacade::map)
                                .collect(Collectors.toList())).getBytes();
                HttpHeaders respHeaders = new HttpHeaders();
                respHeaders.setContentLength(isr.length);
                respHeaders.setContentType(MediaType.parseMediaType("application/octet-stream"));
                respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                String newFileName = StringUtils.substringAfter(FilenameUtils.removeExtension(batchFile.getFileName()), ".")
                        + "_Results."
                        + FilenameUtils.getExtension(batchFile.getFileName());
                respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + newFileName);
                return new ResponseEntity(isr, respHeaders, HttpStatus.OK);
            } else {
                return new ResponseEntity(new Link(null, "File is still processing.  Please check later"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity(new Link(null, "discovery id " + id + " is invalid"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/api/discovery/table", produces = {"application/json"})
    public ResponseEntity<List<DiscoveryTable>> lookupBatchInfo(@RequestParam Integer timeZone) {
        return ResponseEntity.ok(discoveryService.getBatches(timeZone));
    }


    @PostMapping(value = "/track/client/api", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<TrackResponseModel> fromTrackToBPS(@RequestBody TrackRequestModel trackModel) throws
            IOException {
        TrackResponseModel trackResponseModel = new TrackResponseModel();
        TrackResponseModel.MatchScoreData matchScoreData = new TrackResponseModel.MatchScoreData();
        TrackResponseModel.MatchResults matchResults = new TrackResponseModel.MatchResults();
        TrackResponseModel.RequestData requestData = new TrackResponseModel.RequestData();

        if (trackModel.getRequestDetail().get(0).getCompanyName().equalsIgnoreCase("United") ||
                trackModel.getRequestDetail().get(0).getCompanyName().contains("United") ||
                trackModel.getRequestDetail().get(0).getCompanyName().equalsIgnoreCase("BNP") ||
                trackModel.getRequestDetail().get(0).getCompanyName().contains("BNP")) {
            matchScoreData.setMatchPercentage(1);
            matchResults.setMatchStatus("Inconclusive");
            requestData.setTrackId("12323434");
        } else {
            matchScoreData.setMatchPercentage(2);
            matchResults.setMatchStatus("High Confidence");
            requestData.setTrackId("12345567");
        }
        matchResults.setMatchScoreData(matchScoreData);
        TrackResponseModel.ResponseDetail responseDetail = new TrackResponseModel.ResponseDetail();
        responseDetail.setRequestData(requestData);
        responseDetail.setMatchResults(matchResults);
        trackResponseModel.setResponseDetail(singletonList(responseDetail));
        return ResponseEntity.ok(trackResponseModel);
    }

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    static class Link {
        public Link(String url, String... messages) {
            this.url = url;
            this.messages = messages;
        }

        private String url;
        private String[] messages;
    }


}


