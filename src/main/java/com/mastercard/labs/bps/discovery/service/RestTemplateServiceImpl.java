package com.mastercard.labs.bps.discovery.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.labs.bps.discovery.controller.GlobalExceptionHandler;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.domain.journal.Record;
import com.mastercard.labs.bps.discovery.domain.journal.Registration;
import com.mastercard.labs.bps.discovery.exceptions.ExecutionException;
import com.mastercard.labs.bps.discovery.exceptions.ResourceNotFoundException;
import com.mastercard.labs.bps.discovery.exceptions.TrackAuthenticationException;
import com.mastercard.labs.bps.discovery.webhook.model.*;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.BoundMapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RestTemplateServiceImpl {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private BoundMapperFacade<Discovery, TrackRequestModel> discoveryToTrackModel;

    @Autowired
    private BoundMapperFacade<Registration, TrackRequestModel> registrationToTrackModel;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Value("${track.post.singleVendorDetail}")
    private String urlToTrack;

    @Value("${swagger.url.directory}")
    private String directoryPath;

    @Value("${track.auth.url}")
    private String trackAuthUrl;
    @Value("${track.auth.grantType}")
    private String trackAuthGrantType;

    @Value("${track.auth.client.assertionType}")
    private String trackAutAssertionType;

    @Value("${track.auth.client.assertion}")
    private String trackAuthAssertion;

    @Value("${track.auth.client.id}")
    private String trackAuthClientId;

    @Value("${directory.registerBuyer}")
    private String registerBuyer;

    @Value("${directory.registerSupplier}")
    private String registerSupplier;

    @Value("${directory.agentList}")
    private String agentList;

    @Value("${track.auth.scope}")
    private String trackAuthScope;

    @Autowired
    private RestTemplate externalSSLRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private volatile AtomicReference<org.springframework.data.util.Pair<LocalDateTime, String>> tokenExpiration = new AtomicReference<>(org.springframework.data.util.Pair.of(LocalDateTime.MIN, ""));

    public RestTemplate getRestTemplate(String url) {
        return (StringUtils.startsWithIgnoreCase(url, "https")) ? externalSSLRestTemplate : restTemplate;
    }


    public FutureTask<ResponseEntity<TrackResponseModel>> callTrack(Record record) throws ExecutionException, InterruptedException {
        HttpHeaders headers = getHeaders();
        try {
            headers.add("authorization", trackAuthBearer());
            FutureTask<ResponseEntity<TrackResponseModel>> task = new FutureTask<>(() -> {
                try {
                    return getRestTemplate(urlToTrack).exchange(urlToTrack, HttpMethod.POST, new HttpEntity<>(record instanceof Discovery ? discoveryToTrackModel.map((Discovery) record) : registrationToTrackModel.map((Registration) record), headers), TrackResponseModel.class);
                } catch (final HttpClientErrorException e) {
                    log.error("Status Code: " + e.getStatusCode());
                    log.error("Response: " + e.getResponseBodyAsString());
                    throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST, e.getResponseBodyAsString());
                }
            });
            taskExecutor.execute(task);
            return task;
        } catch (TrackAuthenticationException e) {
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public <T> Optional<List<T>> getCompanyFromDirectory(String url, Class<T> clazz) {
        try {
            ResponseEntity<?> responseEntity = getRestTemplate(directoryPath).exchange(directoryPath + url, HttpMethod.GET, new HttpEntity<>(getHeaders()), Object.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(jacksonObjectMapper.readValue(jacksonObjectMapper.writeValueAsString(responseEntity.getBody()), List.class));
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Buyer> registerBuyer(BusinessEntity businessEntity, String bpsId, String agentName) throws ExecutionException {
        return getObjecttOrError(directoryPath + registerBuyer, businessEntity, bpsId, agentName, Buyer.class);
    }

    public Optional<Supplier> registerSupplier(BusinessEntity businessEntity, String bpsId, String agentName) throws ExecutionException {
        return getObjecttOrError(directoryPath + registerSupplier, businessEntity, bpsId, agentName, Supplier.class);
    }

    private <T> Optional<T> getObjecttOrError(String url, BusinessEntity businessEntity, String bpsId, String agentName, Class<T> clazz) {
        try {
            T returns = postRequest(getRestTemplate(directoryPath), url, businessEntity, bpsId, agentName, clazz);
            return Optional.ofNullable(returns);

        } catch (HttpClientErrorException e) {
            log.error(e.getMessage(), e);
            try {
                GlobalExceptionHandler.ErrorData errorData = jacksonObjectMapper.readValue(e.getResponseBodyAsString(), GlobalExceptionHandler.ErrorData.class);
                throw new ExecutionException(errorData.getMessages());
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
                throw new ExecutionException(e1.getMessage());
            }
        } catch (UnsupportedEncodingException e) {
            throw new ExecutionException(e.getMessage());
        }
    }

    private <T> T postRequest(RestTemplate restTemplate, String url, BusinessEntity businessEntity, String bpsId, String agentName, Class<T> clazz) throws HttpClientErrorException, UnsupportedEncodingException {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        return restTemplate.postForObject(url + "?bpsId=" + URLEncoder.encode(bpsId, "UTF-8") + "&" + "agentName=" + URLEncoder.encode(agentName, "UTF-8"), businessEntity, clazz);
    }

    public <T> List<T> getCompaniesFromDirectory(String url, Class<T> clazz) {
        try {
            ResponseEntity<?> responseEntity = getRestTemplate(directoryPath).exchange(directoryPath + url, HttpMethod.GET, new HttpEntity<>(getHeaders()), Object.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return ((Collection<Map>) responseEntity.getBody()).stream().map(m -> {
                    try {
                        return jacksonObjectMapper.readValue(jacksonObjectMapper.writeValueAsString(m), clazz);
                    } catch (IOException e) {
                        log.error("error converting ", e);
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }


    private String trackAuthBearer() {
        if (tokenExpiration.get().getFirst().isEqual(LocalDateTime.MIN) || tokenExpiration.get().getFirst().until(LocalDateTime.now(), ChronoUnit.SECONDS) > -10) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            try {
                String body = Stream.of("grant_type=" + URLEncoder.encode(trackAuthGrantType, "UTF-8"),
                        "client_assertion_type=" + URLEncoder.encode(trackAutAssertionType, "UTF-8"),
                        "client_assertion=" + URLEncoder.encode(trackAuthAssertion, "UTF-8"),
                        "client_id=" + URLEncoder.encode(trackAuthClientId, "UTF-8"),
                        "scope=" + URLEncoder.encode(trackAuthScope, "UTF-8")).collect(Collectors.joining("&"));

                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                ResponseEntity<?> response = getRestTemplate(trackAuthUrl).exchange(trackAuthUrl, HttpMethod.POST, entity, Object.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    int expirationSeconds = ((Map<String, Integer>) response.getBody()).get("ext_expires_in");
                    tokenExpiration.set(Pair.of(LocalDateTime.now().plusSeconds(expirationSeconds), ((Map<String, String>) response.getBody()).get("access_token")));
                } else {
                    throw new TrackAuthenticationException("error obtaining track oauth2 token");
                }
            } catch (Exception e) {
                throw new TrackAuthenticationException(e.getMessage());
            }
        }
        return "Bearer " + tokenExpiration.get().getSecond();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
