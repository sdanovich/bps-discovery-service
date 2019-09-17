package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.domain.journal.Record;
import com.mastercard.labs.bps.discovery.domain.journal.Registration;
import com.mastercard.labs.bps.discovery.webhook.model.*;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Validator;
import java.util.Arrays;
import java.util.UUID;
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
                                buildTrackModel(a, b);
                            }
                        }
                )
                .byDefault()
                .register();

        return mapperFactory.getMapperFacade(Discovery.class, TrackRequestModel.class);
    }

    @Bean
    public BoundMapperFacade<Registration, TrackRequestModel> registrationToTrackModel() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Registration.class, TrackRequestModel.class)
                .customize(
                        new CustomMapper<Registration, TrackRequestModel>() {
                            @Override
                            public void mapAtoB(Registration a, TrackRequestModel b, MappingContext context) {
                                buildTrackModel(a, b);
                            }
                        }
                )
                .byDefault()
                .register();

        return mapperFactory.getMapperFacade(Registration.class, TrackRequestModel.class);
    }

    private void buildTrackModel(Record a, TrackRequestModel b) {
        TrackRequestModel.Address address = new TrackRequestModel.Address();
        address.setAddress1(a.getAddress1());
        address.setAddress2(a.getAddress2());
        address.setAddress3(a.getAddress3());
        address.setCity(a.getCity());
        address.setState(a.getState());
        address.setZip(a.getZip());
        address.setCountry(a.getCountry());
        TrackRequestModel.RequestDetail requestDetail = new TrackRequestModel.RequestDetail();
        requestDetail.setAddress(address);
        requestDetail.setCompanyName(a.getCompanyName());
        requestDetail.setId("1");
        requestDetail.setTin(a.getTaxId());
        requestDetail.setRequestType("premium");
        TrackRequestModel.RequestHeader requestHeader = new TrackRequestModel.RequestHeader();
        requestHeader.setBusinessId(businessId);
        requestHeader.setOrderId(UUID.randomUUID().toString());
        requestHeader.setOrderType("new");
        requestHeader.setMatchType("HIGHCONFIDENCE");
        b.setRequestDetail(Arrays.asList(requestDetail));
        b.setRequestHeader(requestHeader);
    }

    @Bean
    public BoundMapperFacade<Discovery, DiscoveryModelSupplier> discoveryToCSVFull() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Discovery.class, DiscoveryModelSupplier.class)
                .customize(
                        new CustomMapper<Discovery, DiscoveryModelSupplier>() {
                            @Override
                            public void mapAtoB(Discovery a, DiscoveryModelSupplier b, MappingContext context) {
                                discoveryModelService(a, b);
                            }
                        }
                )
                .byDefault()
                .register();
        return mapperFactory.getMapperFacade(Discovery.class, DiscoveryModelSupplier.class);
    }

    @Bean
    public BoundMapperFacade<Discovery, DiscoveryModelBuyer> discoveryToCSVPartial() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Discovery.class, DiscoveryModelBuyer.class)
                .customize(
                        new CustomMapper<Discovery, DiscoveryModelBuyer>() {
                            @Override
                            public void mapAtoB(Discovery a, DiscoveryModelBuyer b, MappingContext context) {
                                discoveryModelService(a, b);
                            }
                        }
                )
                .byDefault()
                .register();
        return mapperFactory.getMapperFacade(Discovery.class, DiscoveryModelBuyer.class);
    }

    @Bean
    public BoundMapperFacade<Registration, RegistrationModelSupplier> registrationToCSVFull() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Registration.class, RegistrationModelSupplier.class)
                .customize(
                        new CustomMapper<Registration, RegistrationModelSupplier>() {
                            @Override
                            public void mapAtoB(Registration a, RegistrationModelSupplier b, MappingContext context) {
                                registrationModelService(a, b);
                            }
                        }
                )
                .byDefault()
                .register();
        return mapperFactory.getMapperFacade(Registration.class, RegistrationModelSupplier.class);
    }


    @Bean
    public BoundMapperFacade<Registration, RegistrationModelBuyer> registrationToCSVPartial() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Registration.class, RegistrationModelBuyer.class)
                .customize(
                        new CustomMapper<Registration, RegistrationModelBuyer>() {
                            @Override
                            public void mapAtoB(Registration a, RegistrationModelBuyer b, MappingContext context) {
                                registrationModelService(a, b);
                            }
                        }
                )
                .byDefault()
                .register();
        return mapperFactory.getMapperFacade(Registration.class, RegistrationModelBuyer.class);
    }

    private void discoveryModelService(Discovery a, Object target) {
        if (target instanceof DiscoveryModelSupplier) {
            getSupplierModel(a, (DiscoveryModelSupplier) target);
        } else if (target instanceof DiscoveryModelBuyer) {
            getBuyerModel(a, (DiscoveryModelBuyer) target);
        }
    }

    private void registrationModelService(Registration a, Object target) {
        if (target instanceof RegistrationModelSupplier) {
            getSupplierModel(a, (RegistrationModelSupplier) target);
        } else if (target instanceof RegistrationModelBuyer) {
            getBuyerModel(a, (RegistrationModelBuyer) target);
        }
    }

    private void getBuyerModel(Record a, Model target) {
        if (a instanceof Discovery) {
            DiscoveryModelBuyer b = (DiscoveryModelBuyer) target;
            b.setAddress1(a.getAddress1());
            b.setAddress2(a.getAddress2());
            b.setAddress3(a.getAddress3());
            b.setBpsAvailable(((Discovery) a).getBpsPresent() != null ? ((Discovery) a).getBpsPresent().name() : "N");
            b.setConfidence(a.getReason() != null ? a.getReason() : a.getConfidence());
            b.setCity(a.getCity());
            b.setCompanyName(a.getCompanyName());
            b.setDbId(a.getDbId());
            b.setTaxId(a.getTaxId());
            b.setState(a.getState());
            b.setZip(a.getZip());
            b.setCountry(a.getCountry());
        } else if (a instanceof Registration) {
            RegistrationModelBuyer b = (RegistrationModelBuyer) target;
            b.setAddress1(a.getAddress1());
            b.setAddress2(a.getAddress2());
            b.setAddress3(a.getAddress3());
            b.setCity(a.getCity());
            b.setCompanyName(a.getCompanyName());
            b.setDbId(a.getDbId());
            b.setTaxId(a.getTaxId());
            b.setState(a.getState());
            b.setZip(a.getZip());
            b.setCountry(a.getCountry());
            b.setStatus(a.getStatus().name());
            b.setReason(a.getReason());
            b.setBuyerId(((Registration) a).getBpsId());
        }
    }

    private void getSupplierModel(Record a, Model target) {
        if (a instanceof Discovery) {
            DiscoveryModelSupplier b = (DiscoveryModelSupplier) target;
            b.setAddress1(a.getAddress1());
            b.setAddress2(a.getAddress2());
            b.setAddress3(a.getAddress3());
            b.setCity(a.getCity());
            b.setCompanyName(a.getCompanyName());
            b.setDbId(a.getDbId());
            b.setTaxId(a.getTaxId());
            b.setState(a.getState());
            b.setZip(a.getZip());
            b.setCountry(a.getCountry());
            Stream<String> ids = restTemplateService.getCompaniesFromDirectory(StringUtils.replace(pathToSuppliersByTaxId, "{taxid}", StringUtils.trim(a.getTaxId())), SupplierAgent.class).stream().map(SupplierAgent::getBpsId);
            b.setRestriction(isRuleRestricted(ids) ? "Y" : "N");
            b.setBpsAvailable(((Discovery) a).getBpsPresent() != null ? ((Discovery) a).getBpsPresent().name() : "N");
            b.setConfidence(a.getReason() != null ? a.getReason() : a.getConfidence());
            b.setCardAcceptable(((Discovery) a).getBpsPresent() != null ? ((Discovery) a).getBpsPresent().name() : "N");
        } else if (a instanceof Registration) {
            RegistrationModelSupplier b = (RegistrationModelSupplier) target;
            b.setAddress1(a.getAddress1());
            b.setAddress2(a.getAddress2());
            b.setAddress3(a.getAddress3());
            b.setCity(a.getCity());
            b.setCompanyName(a.getCompanyName());
            b.setDbId(a.getDbId());
            b.setTaxId(a.getTaxId());
            b.setState(a.getState());
            b.setZip(a.getZip());
            b.setCountry(a.getCountry());
            b.setStatus(a.getStatus().name());
            b.setReason(a.getReason());
            b.setSupplierId(((Registration) a).getBpsId());
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
            ResponseEntity<?> requestData = restTemplateService.getRestTemplate(ruleEngine).exchange(builder.toUriString(),
                    HttpMethod.GET, null, Object.class);
            return requestData.getStatusCode().is2xxSuccessful() ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

}
