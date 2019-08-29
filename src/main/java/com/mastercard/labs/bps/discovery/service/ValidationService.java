package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.webhook.model.DiscoveryModelFull;
import com.mastercard.labs.bps.discovery.webhook.model.DiscoveryModelPartial;
import com.mastercard.labs.bps.discovery.webhook.model.SupplierAgent;
import com.mastercard.labs.bps.discovery.webhook.model.TrackRequestModel;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import javax.validation.Validator;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ValidationService {


    @Value("${discovery.businessId}")
    private String businessId;

    @Value("${swagger.url.rules}")
    private String ruleEngine;

    @Autowired
    private RestTemplateServiceImpl restTemplateService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate externalSSLRestTemplate;

    @Value("${directory.suppliersByTaxId}")
    private String pathToSuppliersByTaxId;

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public BoundMapperFacade<Discovery, TrackRequestModel> discoveryToTrackModel() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Discovery.class, TrackRequestModel.class)
                .customize(
                        new CustomMapper<Discovery, TrackRequestModel>() {
                            @Override
                            public void mapAtoB(Discovery a, TrackRequestModel b, MappingContext context) {
                                TrackRequestModel.Address address = new TrackRequestModel.Address();
                                address.setAddress1(a.getAddress1());
                                address.setAddress2(a.getAddress2());
                                address.setAddress3(a.getAddress3());
                                address.setAddress4(a.getAddress4());
                                address.setCity(a.getCity());
                                address.setState(a.getState());
                                address.setZip(a.getZip());
                                address.setCountry(a.getCountry());
                                TrackRequestModel.RequestDetail requestDetail = new TrackRequestModel.RequestDetail();
                                requestDetail.setAddress(address);
                                requestDetail.setCompanyName(a.getCompanyName());
                                requestDetail.setId(a.getId());
                                requestDetail.setTin(a.getTaxId());
                                TrackRequestModel.RequestHeader requestHeader = new TrackRequestModel.RequestHeader();
                                requestHeader.setBusinessId(businessId);
                                requestHeader.setOrderId(a.getId());
                                requestHeader.setOrderType("new");
                                requestHeader.setMatchType("highconfidence");
                                b.setRequestDetail(Arrays.asList(requestDetail));
                                b.setRequestHeader(requestHeader);
                            }
                        }
                )
                .byDefault()
                .register();

        return mapperFactory.getMapperFacade(Discovery.class, TrackRequestModel.class);
    }

    @Bean
    public BoundMapperFacade<Discovery, DiscoveryModelFull> discoveryToCSVFull() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Discovery.class, DiscoveryModelFull.class)
                .customize(
                        new CustomMapper<Discovery, DiscoveryModelFull>() {
                            @Override
                            public void mapAtoB(Discovery a, DiscoveryModelFull b, MappingContext context) {
                                discoveryModelService(a, b);
                            }
                        }
                )
                .byDefault()
                .register();
        return mapperFactory.getMapperFacade(Discovery.class, DiscoveryModelFull.class);
    }

    @Bean
    public BoundMapperFacade<Discovery, DiscoveryModelPartial> discoveryToCSVPartial() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Discovery.class, DiscoveryModelPartial.class)
                .customize(
                        new CustomMapper<Discovery, DiscoveryModelPartial>() {
                            @Override
                            public void mapAtoB(Discovery a, DiscoveryModelPartial b, MappingContext context) {
                                discoveryModelService(a, b);
                            }
                        }
                )
                .byDefault()
                .register();
        return mapperFactory.getMapperFacade(Discovery.class, DiscoveryModelPartial.class);
    }

    private void discoveryModelService(Discovery a, Object target) {
        if (target instanceof DiscoveryModelFull) {
            DiscoveryModelFull b = (DiscoveryModelFull) target;
            b.setAddressLine1(a.getAddress1());
            b.setAddressLine2(a.getAddress2());
            b.setAddressLine3(a.getAddress3());
            b.setBpsAvailable(a.getBpsPresent() != null ? a.getBpsPresent().name() : "N");
            b.setConfidence(a.getReason() != null ? a.getReason() : a.getConfidence());
            b.setCity(a.getCity());
            b.setCompanyName(a.getCompanyName());
            b.setDbId(a.getDbId());
            b.setTaxId(a.getTaxId());
            b.setState(a.getState());
            b.setZip(a.getZip());
            b.setCountry(a.getCountry());
            Stream<String> ids = restTemplateService.getCompaniesFromDirectory(org.apache.commons.lang3.StringUtils.replace(pathToSuppliersByTaxId, "{taxid}", a.getTaxId()), SupplierAgent.class).stream().map(SupplierAgent::getBpsId);
            b.setRestriction(isRuleRestricted(ids) ? "Y" : "N");
            b.setCardAcceptable(a.getBpsPresent() != null ? a.getBpsPresent().name() : "N");
        } else if (target instanceof DiscoveryModelPartial) {
            DiscoveryModelPartial b = (DiscoveryModelPartial) target;
            b.setAddressLine1(a.getAddress1());
            b.setAddressLine2(a.getAddress2());
            b.setAddressLine3(a.getAddress3());
            b.setBpsAvailable(a.getBpsPresent() != null ? a.getBpsPresent().name() : "N");
            b.setConfidence(a.getReason() != null ? a.getReason() : a.getConfidence());
            b.setCity(a.getCity());
            b.setCompanyName(a.getCompanyName());
            b.setDbId(a.getDbId());
            b.setTaxId(a.getTaxId());
            b.setState(a.getState());
            b.setZip(a.getZip());
            b.setCountry(a.getCountry());
        }

    }

    private boolean isRuleRestricted(Stream<String> ids) {
        String collectIds = ids.collect(Collectors.joining(","));
        if (StringUtils.isEmpty(collectIds)) {
            return false;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ruleEngine + "/rules/existForSuppliers")
                .queryParam("supplierProfileId", collectIds);
        try {
            ResponseEntity<String> requestData = ((ruleEngine.startsWith("https")) ? externalSSLRestTemplate : restTemplate).exchange(builder.toUriString(),
                    HttpMethod.GET, null, String.class);
            return requestData.getStatusCode().is2xxSuccessful() ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

}
