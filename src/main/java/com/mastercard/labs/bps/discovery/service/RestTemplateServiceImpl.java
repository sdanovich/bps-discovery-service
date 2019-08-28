package com.mastercard.labs.bps.discovery.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.exceptions.ResourceNotFoundException;
import com.mastercard.labs.bps.discovery.persistence.support.RequestResponseLoggingInterceptor;
import com.mastercard.labs.bps.discovery.webhook.model.TrackRequestModel;
import com.mastercard.labs.bps.discovery.webhook.model.TrackResponseModel;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.BoundMapperFacade;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestTemplateServiceImpl implements RestTemplateService {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private BoundMapperFacade<Discovery, TrackRequestModel> discoveryToTrackModel;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RequestResponseLoggingInterceptor requestResponseLoggingInterceptor;

    @Value("${discovery.trackPostUrl}")
    private String urlToTrack;

    @Value("${swagger.url.directory}")
    private String directoryPath;


    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return setInterceptorAndErrorHandler(new RestTemplate(requestFactory));
    }

    public FutureTask<ResponseEntity<TrackResponseModel>> callTrack(Discovery discovery) throws ExecutionException, InterruptedException {
        FutureTask<ResponseEntity<TrackResponseModel>> task = new FutureTask<>(() -> {
            try {
                return restTemplate().exchange(urlToTrack, HttpMethod.POST, new HttpEntity<>(discoveryToTrackModel.map(discovery), getHeaders()), TrackResponseModel.class);
            } catch (final HttpClientErrorException e) {
                log.error("Status Code: " + e.getStatusCode());
                log.error("Response: " + e.getResponseBodyAsString());
                throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST, e.getResponseBodyAsString());
            }
        });
        taskExecutor.execute(task);
        return task;
    }

    public <T> Optional<T> getCompanyFromDirectory(String url, Class<T> clazz) {
        try {
            ResponseEntity<?> responseEntity = restTemplate().exchange(directoryPath + url, HttpMethod.GET, new HttpEntity<>(getHeaders()), Object.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(jacksonObjectMapper.readValue(jacksonObjectMapper.writeValueAsString(responseEntity.getBody()), clazz));
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public <T> List<T> getCompaniesFromDirectory(String url, Class<T> clazz) {
        try {
            ResponseEntity<?> responseEntity = restTemplate().exchange(directoryPath + url, HttpMethod.GET, new HttpEntity<>(getHeaders()), Object.class);
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


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private RestTemplate setInterceptorAndErrorHandler(RestTemplate restTemplate) {
        restTemplate.setInterceptors(Collections.singletonList(requestResponseLoggingInterceptor));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });
        return restTemplate;
    }

}
