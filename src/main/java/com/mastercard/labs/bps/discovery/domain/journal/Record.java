package com.mastercard.labs.bps.discovery.domain.journal;

public interface Record {

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

    String getTrackId();

    void setTrackId(String trackId);

    String getConfidence();

    void setConfidence(String confidence);

    String getReason();

    void setReason(String reason);

    Discovery.STATUS getStatus();

    void setStatus(Discovery.STATUS status);

    Discovery.EXISTS getFound();

    void setFound(Discovery.EXISTS exists);
}
