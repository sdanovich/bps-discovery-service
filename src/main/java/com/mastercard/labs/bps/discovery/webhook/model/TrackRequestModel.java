
package com.mastercard.labs.bps.discovery.webhook.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
public class TrackRequestModel {
    private List<RequestDetail> requestDetail;
    private RequestHeader requestHeader;

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestDetail {
        private Address address;
        private String tin;
        private String companyName;
        private String id;
        private String requestType;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class RequestHeader {
        private String orderId;
        private String businessId;
        private String orderType;
        private String matchType;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Address {
        private String city;
        private String country;
        private String state;
        private String address1;
        private String address2;
        private String address3;
        private String address4;
        private String zip;
    }
}


