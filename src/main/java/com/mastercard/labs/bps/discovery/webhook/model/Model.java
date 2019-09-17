package com.mastercard.labs.bps.discovery.webhook.model;


public interface Model {

    String getCompanyName();

    void setCompanyName(String companyName);

    String getAddress1();

    void setAddress1(String address1);

    String getAddress2();

    void setAddress2(String address2);

    String getAddress3();

    void setAddress3(String address3);

    String getCity();

    void setCity(String city);

    String getState();

    void setState(String state);

    String getCountry();

    void setCountry(String country);

    String getZip();

    void setZip(String zip);

    String getTaxId();

    void setTaxId(String taxId);

    String getDbId();

    void setDbId(String dbId);

    String getScore();

    void setScore(String score);

    String getBusinessName();

    void setBusinessName(String businessName);

    String getStreetAddress();

    void setStreetAddress(String streetAddress);

    String getTrackCity();

    void setTrackCity(String trackCity);

    String getTrackState();

    void setTrackState(String trackState);

    String getTrackZip();

    void setTrackZip(String trackZip);

    String getTrackId();

    void setTrackId(String trackId);

}

