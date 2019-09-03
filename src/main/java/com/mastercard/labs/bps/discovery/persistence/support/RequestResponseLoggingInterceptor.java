package com.mastercard.labs.bps.discovery.persistence.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Value("${webhook.headerName}")
    private String webhookHeaderName;


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }

    public void logRequest(HttpRequest request, byte[] body) throws IOException {
        //if (log.isDebugEnabled()) {
        log.debug("===========================request begin================================================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        log.debug("Headers     : {}", getHeaders(request.getHeaders()));
        log.debug("Request body: {}", getBody(body));
        log.debug("==========================request end================================================");
        //}
    }

    public void logResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, String body) throws IOException {
        //if (log.isDebugEnabled()) {
        log.debug("============================response begin==========================================");
        log.debug("Status code  : {}", "" + httpStatus);
        log.debug("Headers      : {}", getHeaders(httpHeaders));
        log.debug("Response body: {}", getBody(body.getBytes()));
        log.debug("=======================response end=================================================");
        //}
    }

    private String getBody(byte[] body) throws IOException {
        try {
            JsonNode rootNode = jacksonObjectMapper.readTree(new String(body, "UTF-8"));
            return jacksonObjectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            return new String(body);
        }
    }

    private HttpHeaders getHeaders(HttpHeaders headers) throws IOException {
        HttpHeaders logHeaders = new HttpHeaders();
        headers.keySet().stream().forEach(s -> {
            if (s.equalsIgnoreCase(webhookHeaderName)) {
                logHeaders.put(s, headers.get(s));
            }
        });
        return logHeaders;
    }
}
