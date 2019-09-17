package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "sic",
        "naics"
})
@Getter
@Setter
class BusinessClassification implements Serializable {

    @JsonProperty("sic")
    private Sic sic;
    @JsonProperty("naics")
    private Naics naics;
    private final static long serialVersionUID = 1115435340012052905L;

    /**
     * No args constructor for use in serialization
     */
    public BusinessClassification() {
    }

    /**
     * @param sic
     * @param naics
     */
    public BusinessClassification(Sic sic, Naics naics) {
        super();
        this.sic = sic;
        this.naics = naics;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sic", sic).append("naics", naics).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sic).append(naics).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof BusinessClassification) == false) {
            return false;
        }
        BusinessClassification rhs = ((BusinessClassification) other);
        return new EqualsBuilder().append(sic, rhs.sic).append(naics, rhs.naics).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "referenceid"
})
@Getter
@Setter
class Compliance implements Serializable {

    @JsonProperty("referenceid")
    private String referenceid;
    private final static long serialVersionUID = -3631745078457509879L;

    /**
     * No args constructor for use in serialization
     */
    public Compliance() {
    }

    /**
     * @param referenceid
     */
    public Compliance(String referenceid) {
        super();
        this.referenceid = referenceid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("referenceid", referenceid).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(referenceid).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Compliance) == false) {
            return false;
        }
        Compliance rhs = ((Compliance) other);
        return new EqualsBuilder().append(referenceid, rhs.referenceid).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "complianceDataFlag",
        "complianceDetails"
})
@Getter
@Setter
class ComplianceData implements Serializable {

    @JsonProperty("complianceDataFlag")
    private String complianceDataFlag;
    @JsonProperty("complianceDetails")
    private Object complianceDetails;
    private final static long serialVersionUID = 8376813105615522344L;

    /**
     * No args constructor for use in serialization
     */
    public ComplianceData() {
    }

    /**
     * @param complianceDetails
     * @param complianceDataFlag
     */
    public ComplianceData(String complianceDataFlag, Object complianceDetails) {
        super();
        this.complianceDataFlag = complianceDataFlag;
        this.complianceDetails = complianceDetails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("complianceDataFlag", complianceDataFlag).append("complianceDetails", complianceDetails).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(complianceDetails).append(complianceDataFlag).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ComplianceData) == false) {
            return false;
        }
        ComplianceData rhs = ((ComplianceData) other);
        return new EqualsBuilder().append(complianceDetails, rhs.complianceDetails).append(complianceDataFlag, rhs.complianceDataFlag).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "businessName",
        "country",
        "businessId"
})
@Getter
@Setter
class CorporateHierachyDetails implements Serializable {

    @JsonProperty("businessName")
    private Object businessName;
    @JsonProperty("country")
    private Object country;
    @JsonProperty("businessId")
    private Object businessId;
    private final static long serialVersionUID = -3181345243950276699L;

    /**
     * No args constructor for use in serialization
     */
    public CorporateHierachyDetails() {
    }

    /**
     * @param businessName
     * @param businessId
     * @param country
     */
    public CorporateHierachyDetails(Object businessName, Object country, Object businessId) {
        super();
        this.businessName = businessName;
        this.country = country;
        this.businessId = businessId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("businessName", businessName).append("country", country).append("businessId", businessId).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(businessName).append(businessId).append(country).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CorporateHierachyDetails) == false) {
            return false;
        }
        CorporateHierachyDetails rhs = ((CorporateHierachyDetails) other);
        return new EqualsBuilder().append(businessName, rhs.businessName).append(businessId, rhs.businessId).append(country, rhs.country).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "corporateHierarchyDataFlag",
        "corporateHierachyDetails"
})
@Getter
@Setter
class CorporateHierarchyData implements Serializable {

    @JsonProperty("corporateHierarchyDataFlag")
    private Object corporateHierarchyDataFlag;
    @JsonProperty("corporateHierachyDetails")
    private CorporateHierachyDetails corporateHierachyDetails;
    private final static long serialVersionUID = 1353313529457139850L;

    /**
     * No args constructor for use in serialization
     */
    public CorporateHierarchyData() {
    }

    /**
     * @param corporateHierarchyDataFlag
     * @param corporateHierachyDetails
     */
    public CorporateHierarchyData(Object corporateHierarchyDataFlag, CorporateHierachyDetails corporateHierachyDetails) {
        super();
        this.corporateHierarchyDataFlag = corporateHierarchyDataFlag;
        this.corporateHierachyDetails = corporateHierachyDetails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("corporateHierarchyDataFlag", corporateHierarchyDataFlag).append("corporateHierachyDetails", corporateHierachyDetails).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(corporateHierarchyDataFlag).append(corporateHierachyDetails).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CorporateHierarchyData) == false) {
            return false;
        }
        CorporateHierarchyData rhs = ((CorporateHierarchyData) other);
        return new EqualsBuilder().append(corporateHierarchyDataFlag, rhs.corporateHierarchyDataFlag).append(corporateHierachyDetails, rhs.corporateHierachyDetails).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "creditRatingDataFlag",
        "creditRating"
})
@Getter
@Setter
class CreditRiskData implements Serializable {

    @JsonProperty("creditRatingDataFlag")
    private String creditRatingDataFlag;
    @JsonProperty("creditRating")
    private Object creditRating;
    private final static long serialVersionUID = -4580888080753346447L;

    /**
     * No args constructor for use in serialization
     */
    public CreditRiskData() {
    }

    /**
     * @param creditRating
     * @param creditRatingDataFlag
     */
    public CreditRiskData(String creditRatingDataFlag, Object creditRating) {
        super();
        this.creditRatingDataFlag = creditRatingDataFlag;
        this.creditRating = creditRating;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("creditRatingDataFlag", creditRatingDataFlag).append("creditRating", creditRating).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(creditRating).append(creditRatingDataFlag).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CreditRiskData) == false) {
            return false;
        }
        CreditRiskData rhs = ((CreditRiskData) other);
        return new EqualsBuilder().append(creditRating, rhs.creditRating).append(creditRatingDataFlag, rhs.creditRatingDataFlag).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "customfield1",
        "customfield2",
        "customfield3",
        "customfield4",
        "customfield5",
        "customfield6",
        "customfield7",
        "customfield8",
        "customfield9",
        "customfield10"
})
@Getter
@Setter
class CustomFields implements Serializable {

    @JsonProperty("customfield1")
    private String customfield1;
    @JsonProperty("customfield2")
    private String customfield2;
    @JsonProperty("customfield3")
    private String customfield3;
    @JsonProperty("customfield4")
    private String customfield4;
    @JsonProperty("customfield5")
    private String customfield5;
    @JsonProperty("customfield6")
    private String customfield6;
    @JsonProperty("customfield7")
    private String customfield7;
    @JsonProperty("customfield8")
    private String customfield8;
    @JsonProperty("customfield9")
    private String customfield9;
    @JsonProperty("customfield10")
    private String customfield10;
    private final static long serialVersionUID = -8098243261930510226L;

    /**
     * No args constructor for use in serialization
     */
    public CustomFields() {
    }

    /**
     * @param customfield3
     * @param customfield4
     * @param customfield1
     * @param customfield2
     * @param customfield10
     * @param customfield7
     * @param customfield8
     * @param customfield5
     * @param customfield6
     * @param customfield9
     */
    public CustomFields(String customfield1, String customfield2, String customfield3, String customfield4, String customfield5, String customfield6, String customfield7, String customfield8, String customfield9, String customfield10) {
        super();
        this.customfield1 = customfield1;
        this.customfield2 = customfield2;
        this.customfield3 = customfield3;
        this.customfield4 = customfield4;
        this.customfield5 = customfield5;
        this.customfield6 = customfield6;
        this.customfield7 = customfield7;
        this.customfield8 = customfield8;
        this.customfield9 = customfield9;
        this.customfield10 = customfield10;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("customfield1", customfield1).append("customfield2", customfield2).append("customfield3", customfield3).append("customfield4", customfield4).append("customfield5", customfield5).append("customfield6", customfield6).append("customfield7", customfield7).append("customfield8", customfield8).append("customfield9", customfield9).append("customfield10", customfield10).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(customfield3).append(customfield4).append(customfield1).append(customfield2).append(customfield10).append(customfield7).append(customfield8).append(customfield5).append(customfield6).append(customfield9).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CustomFields) == false) {
            return false;
        }
        CustomFields rhs = ((CustomFields) other);
        return new EqualsBuilder().append(customfield3, rhs.customfield3).append(customfield4, rhs.customfield4).append(customfield1, rhs.customfield1).append(customfield2, rhs.customfield2).append(customfield10, rhs.customfield10).append(customfield7, rhs.customfield7).append(customfield8, rhs.customfield8).append(customfield5, rhs.customfield5).append(customfield6, rhs.customfield6).append(customfield9, rhs.customfield9).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "employeesDataFlag",
        "noofLocalEmployees",
        "noofGlobalEmployees"
})
@Getter
@Setter
class EmployeesData implements Serializable {

    @JsonProperty("employeesDataFlag")
    private String employeesDataFlag;
    @JsonProperty("noofLocalEmployees")
    private Object noofLocalEmployees;
    @JsonProperty("noofGlobalEmployees")
    private Object noofGlobalEmployees;
    private final static long serialVersionUID = 1236297958277528781L;

    /**
     * No args constructor for use in serialization
     */
    public EmployeesData() {
    }

    /**
     * @param noofLocalEmployees
     * @param noofGlobalEmployees
     * @param employeesDataFlag
     */
    public EmployeesData(String employeesDataFlag, Object noofLocalEmployees, Object noofGlobalEmployees) {
        super();
        this.employeesDataFlag = employeesDataFlag;
        this.noofLocalEmployees = noofLocalEmployees;
        this.noofGlobalEmployees = noofGlobalEmployees;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("employeesDataFlag", employeesDataFlag).append("noofLocalEmployees", noofLocalEmployees).append("noofGlobalEmployees", noofGlobalEmployees).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(noofLocalEmployees).append(noofGlobalEmployees).append(employeesDataFlag).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof EmployeesData) == false) {
            return false;
        }
        EmployeesData rhs = ((EmployeesData) other);
        return new EqualsBuilder().append(noofLocalEmployees, rhs.noofLocalEmployees).append(noofGlobalEmployees, rhs.noofGlobalEmployees).append(employeesDataFlag, rhs.employeesDataFlag).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "executivesDataFlag",
        "executivesDetails"
})
@Getter
@Setter
class ExecutivesData implements Serializable {

    @JsonProperty("executivesDataFlag")
    private String executivesDataFlag;
    @JsonProperty("executivesDetails")
    private List<ExecutivesDetail> executivesDetails = null;
    private final static long serialVersionUID = 1543073054611630189L;

    /**
     * No args constructor for use in serialization
     */
    public ExecutivesData() {
    }

    /**
     * @param executivesDetails
     * @param executivesDataFlag
     */
    public ExecutivesData(String executivesDataFlag, List<ExecutivesDetail> executivesDetails) {
        super();
        this.executivesDataFlag = executivesDataFlag;
        this.executivesDetails = executivesDetails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("executivesDataFlag", executivesDataFlag).append("executivesDetails", executivesDetails).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(executivesDetails).append(executivesDataFlag).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ExecutivesData) == false) {
            return false;
        }
        ExecutivesData rhs = ((ExecutivesData) other);
        return new EqualsBuilder().append(executivesDetails, rhs.executivesDetails).append(executivesDataFlag, rhs.executivesDataFlag).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "firstName",
        "lastName",
        "title",
        "address"
})
@Getter
@Setter
class ExecutivesDetail implements Serializable {

    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("title")
    private String title;
    @JsonProperty("address")
    private TrackResponseModel.Address address;
    private final static long serialVersionUID = -7731072526241536837L;

    /**
     * No args constructor for use in serialization
     */
    public ExecutivesDetail() {
    }

    /**
     * @param lastName
     * @param title
     * @param address
     * @param firstName
     */
    public ExecutivesDetail(String firstName, String lastName, String title, TrackResponseModel.Address address) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.address = address;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("firstName", firstName).append("lastName", lastName).append("title", title).append("address", address).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lastName).append(title).append(address).append(firstName).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ExecutivesDetail) == false) {
            return false;
        }
        ExecutivesDetail rhs = ((ExecutivesDetail) other);
        return new EqualsBuilder().append(lastName, rhs.lastName).append(title, rhs.title).append(address, rhs.address).append(firstName, rhs.firstName).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "linkStatus"
})
@Getter
@Setter
class LinkDetails implements Serializable {

    @JsonProperty("linkStatus")
    private Object linkStatus;
    private final static long serialVersionUID = 4961668513351100083L;

    /**
     * No args constructor for use in serialization
     */
    public LinkDetails() {
    }

    /**
     * @param linkStatus
     */
    public LinkDetails(Object linkStatus) {
        super();
        this.linkStatus = linkStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("linkStatus", linkStatus).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(linkStatus).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LinkDetails) == false) {
            return false;
        }
        LinkDetails rhs = ((LinkDetails) other);
        return new EqualsBuilder().append(linkStatus, rhs.linkStatus).isEquals();
    }

}


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "totalRecords",
        "errorRecords",
        "highConfidence",
        "likelyMatches",
        "noMatch",
        "duplicates"
})
@Getter
@Setter
class MatchStatistics implements Serializable {

    @JsonProperty("totalRecords")
    private Integer totalRecords;
    @JsonProperty("errorRecords")
    private Integer errorRecords;
    @JsonProperty("highConfidence")
    private Integer highConfidence;
    @JsonProperty("likelyMatches")
    private Integer likelyMatches;
    @JsonProperty("noMatch")
    private Integer noMatch;
    @JsonProperty("duplicates")
    private Integer duplicates;
    private final static long serialVersionUID = -4551061140331612471L;

    /**
     * No args constructor for use in serialization
     */
    public MatchStatistics() {
    }

    /**
     * @param errorRecords
     * @param duplicates
     * @param noMatch
     * @param totalRecords
     * @param likelyMatches
     * @param highConfidence
     */
    public MatchStatistics(Integer totalRecords, Integer errorRecords, Integer highConfidence, Integer likelyMatches, Integer noMatch, Integer duplicates) {
        super();
        this.totalRecords = totalRecords;
        this.errorRecords = errorRecords;
        this.highConfidence = highConfidence;
        this.likelyMatches = likelyMatches;
        this.noMatch = noMatch;
        this.duplicates = duplicates;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("totalRecords", totalRecords).append("errorRecords", errorRecords).append("highConfidence", highConfidence).append("likelyMatches", likelyMatches).append("noMatch", noMatch).append("duplicates", duplicates).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(errorRecords).append(duplicates).append(noMatch).append(totalRecords).append(likelyMatches).append(highConfidence).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MatchStatistics) == false) {
            return false;
        }
        MatchStatistics rhs = ((MatchStatistics) other);
        return new EqualsBuilder().append(errorRecords, rhs.errorRecords).append(duplicates, rhs.duplicates).append(noMatch, rhs.noMatch).append(totalRecords, rhs.totalRecords).append(likelyMatches, rhs.likelyMatches).append(highConfidence, rhs.highConfidence).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "primaryClassification",
        "primaryCode",
        "secondaryClassification",
        "secondaryCode"
})
@Getter
@Setter
class Naics implements Serializable {

    @JsonProperty("primaryClassification")
    private String primaryClassification;
    @JsonProperty("primaryCode")
    private String primaryCode;
    @JsonProperty("secondaryClassification")
    private String secondaryClassification;
    @JsonProperty("secondaryCode")
    private String secondaryCode;
    private final static long serialVersionUID = 8955719640518093652L;

    /**
     * No args constructor for use in serialization
     */
    public Naics() {
    }

    /**
     * @param secondaryCode
     * @param secondaryClassification
     * @param primaryClassification
     * @param primaryCode
     */
    public Naics(String primaryClassification, String primaryCode, String secondaryClassification, String secondaryCode) {
        super();
        this.primaryClassification = primaryClassification;
        this.primaryCode = primaryCode;
        this.secondaryClassification = secondaryClassification;
        this.secondaryCode = secondaryCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("primaryClassification", primaryClassification).append("primaryCode", primaryCode).append("secondaryClassification", secondaryClassification).append("secondaryCode", secondaryCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(secondaryCode).append(secondaryClassification).append(primaryClassification).append(primaryCode).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Naics) == false) {
            return false;
        }
        Naics rhs = ((Naics) other);
        return new EqualsBuilder().append(secondaryCode, rhs.secondaryCode).append(secondaryClassification, rhs.secondaryClassification).append(primaryClassification, rhs.primaryClassification).append(primaryCode, rhs.primaryCode).isEquals();
    }

}


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "orderId",
        "matchStatistics",
        "errorData"
})
@Getter
@Setter
class ResponseHeader implements Serializable {

    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("matchStatistics")
    private MatchStatistics matchStatistics;
    @JsonProperty("errorData")
    private List<Object> errorData = null;
    private final static long serialVersionUID = -163367708454057029L;

    /**
     * No args constructor for use in serialization
     */
    public ResponseHeader() {
    }

    /**
     * @param errorData
     * @param matchStatistics
     * @param orderId
     */
    public ResponseHeader(String orderId, MatchStatistics matchStatistics, List<Object> errorData) {
        super();
        this.orderId = orderId;
        this.matchStatistics = matchStatistics;
        this.errorData = errorData;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("orderId", orderId).append("matchStatistics", matchStatistics).append("errorData", errorData).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(errorData).append(matchStatistics).append(orderId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ResponseHeader) == false) {
            return false;
        }
        ResponseHeader rhs = ((ResponseHeader) other);
        return new EqualsBuilder().append(errorData, rhs.errorData).append(matchStatistics, rhs.matchStatistics).append(orderId, rhs.orderId).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "revenueDataFlag",
        "revenue"
})
@Getter
@Setter
class RevenueData implements Serializable {

    @JsonProperty("revenueDataFlag")
    private String revenueDataFlag;
    @JsonProperty("revenue")
    private Object revenue;
    private final static long serialVersionUID = -8565461595392510091L;

    /**
     * No args constructor for use in serialization
     */
    public RevenueData() {
    }

    /**
     * @param revenueDataFlag
     * @param revenue
     */
    public RevenueData(String revenueDataFlag, Object revenue) {
        super();
        this.revenueDataFlag = revenueDataFlag;
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("revenueDataFlag", revenueDataFlag).append("revenue", revenue).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(revenueDataFlag).append(revenue).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RevenueData) == false) {
            return false;
        }
        RevenueData rhs = ((RevenueData) other);
        return new EqualsBuilder().append(revenueDataFlag, rhs.revenueDataFlag).append(revenue, rhs.revenue).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "primaryClassification",
        "primaryCode",
        "secondaryClassification",
        "secondaryCode"
})
@Getter
@Setter
class Sic implements Serializable {

    @JsonProperty("primaryClassification")
    private String primaryClassification;
    @JsonProperty("primaryCode")
    private String primaryCode;
    @JsonProperty("secondaryClassification")
    private String secondaryClassification;
    @JsonProperty("secondaryCode")
    private String secondaryCode;
    private final static long serialVersionUID = 1319463456556050301L;

    /**
     * No args constructor for use in serialization
     */
    public Sic() {
    }

    /**
     * @param secondaryCode
     * @param secondaryClassification
     * @param primaryClassification
     * @param primaryCode
     */
    public Sic(String primaryClassification, String primaryCode, String secondaryClassification, String secondaryCode) {
        super();
        this.primaryClassification = primaryClassification;
        this.primaryCode = primaryCode;
        this.secondaryClassification = secondaryClassification;
        this.secondaryCode = secondaryCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("primaryClassification", primaryClassification).append("primaryCode", primaryCode).append("secondaryClassification", secondaryClassification).append("secondaryCode", secondaryCode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(secondaryCode).append(secondaryClassification).append(primaryClassification).append(primaryCode).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Sic) == false) {
            return false;
        }
        Sic rhs = ((Sic) other);
        return new EqualsBuilder().append(secondaryCode, rhs.secondaryCode).append(secondaryClassification, rhs.secondaryClassification).append(primaryClassification, rhs.primaryClassification).append(primaryCode, rhs.primaryCode).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "responseHeader",
        "responseDetail",
        "result",
        "statusCode",
        "status"
})
@Getter
@Setter
@NoArgsConstructor
public class TrackResponseModel implements Serializable {

    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader;
    @JsonProperty("responseDetail")
    private List<ResponseDetail> responseDetail = null;
    @JsonProperty("result")
    private String result;
    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private Message message;
    private final static long serialVersionUID = 1159759687617014115L;


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("responseHeader", responseHeader).append("responseDetail", responseDetail).append("result", result).append("statusCode", statusCode).append("status", status).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(statusCode).append(result).append(responseHeader).append(status).append(responseDetail).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TrackResponseModel) == false) {
            return false;
        }
        TrackResponseModel rhs = ((TrackResponseModel) other);
        return new EqualsBuilder().append(statusCode, rhs.statusCode).append(result, rhs.result).append(responseHeader, rhs.responseHeader).append(status, rhs.status).append(responseDetail, rhs.responseDetail).isEquals();
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisteredBusinessData implements Serializable {

        @JsonProperty("trackId")
        private String trackId;
        @JsonProperty("profileLink")
        private String profileLink;
        @JsonProperty("businessName")
        private String businessName;
        @JsonProperty("address")
        private TrackResponseModel.Address address;
        @JsonProperty("phone")
        private Object phone;
        @JsonProperty("status")
        private String status;
        @JsonProperty("url")
        private String url;
        @JsonProperty("tin")
        private Object tin;
        @JsonProperty("vat")
        private Object vat;
        @JsonProperty("registrationNumber")
        private Object registrationNumber;
        @JsonProperty("operationStartDate")
        private String operationStartDate;
        @JsonProperty("registrationDate")
        private Object registrationDate;
        @JsonProperty("businessClassification")
        private BusinessClassification businessClassification;
        @JsonProperty("businessDescription")
        private Object businessDescription;
        @JsonProperty("employeesData")
        private EmployeesData employeesData;
        @JsonProperty("locationType")
        private String locationType;
        private final static long serialVersionUID = -9222608833176745231L;


        @Override
        public String toString() {
            return new ToStringBuilder(this).append("trackId", trackId).append("profileLink", profileLink).append("businessName", businessName).append("address", address).append("phone", phone).append("status", status).append("url", url).append("tin", tin).append("vat", vat).append("registrationNumber", registrationNumber).append("operationStartDate", operationStartDate).append("registrationDate", registrationDate).append("businessClassification", businessClassification).append("businessDescription", businessDescription).append("employeesData", employeesData).append("locationType", locationType).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(operationStartDate).append(businessName).append(phone).append(trackId).append(employeesData).append(status).append(vat).append(businessDescription).append(tin).append(profileLink).append(url).append(registrationNumber).append(registrationDate).append(businessClassification).append(address).append(locationType).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof RegisteredBusinessData) == false) {
                return false;
            }
            RegisteredBusinessData rhs = ((RegisteredBusinessData) other);
            return new EqualsBuilder().append(operationStartDate, rhs.operationStartDate).append(businessName, rhs.businessName).append(phone, rhs.phone).append(trackId, rhs.trackId).append(employeesData, rhs.employeesData).append(status, rhs.status).append(vat, rhs.vat).append(businessDescription, rhs.businessDescription).append(tin, rhs.tin).append(profileLink, rhs.profileLink).append(url, rhs.url).append(registrationNumber, rhs.registrationNumber).append(registrationDate, rhs.registrationDate).append(businessClassification, rhs.businessClassification).append(address, rhs.address).append(locationType, rhs.locationType).isEquals();
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "companyName",
            "trackId",
            "address",
            "phone",
            "url",
            "contact",
            "ein",
            "tin",
            "vat",
            "registrationNumber",
            "link",
            "customFields"
    })
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestData implements Serializable {

        @JsonProperty("companyName")
        private String companyName;
        @JsonProperty("trackId")
        private String trackId;
        @JsonProperty("address")
        private Address address;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("url")
        private String url;
        @JsonProperty("contact")
        private String contact;
        @JsonProperty("ein")
        private String ein;
        @JsonProperty("tin")
        private String tin;
        @JsonProperty("vat")
        private String vat;
        @JsonProperty("registrationNumber")
        private String registrationNumber;
        @JsonProperty("link")
        private Link link;
        @JsonProperty("customFields")
        private CustomFields customFields;
        private final static long serialVersionUID = -3678371725977270727L;


        @Override
        public String toString() {
            return new ToStringBuilder(this).append("companyName", companyName).append("trackId", trackId).append("address", address).append("phone", phone).append("url", url).append("contact", contact).append("ein", ein).append("tin", tin).append("vat", vat).append("registrationNumber", registrationNumber).append("link", link).append("customFields", customFields).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(phone).append(trackId).append(customFields).append(link).append(ein).append(vat).append(tin).append(companyName).append(contact).append(url).append(registrationNumber).append(address).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof RequestData)) {
                return false;
            }
            RequestData rhs = ((RequestData) other);
            return new EqualsBuilder().append(phone, rhs.phone).append(trackId, rhs.trackId).append(customFields, rhs.customFields).append(link, rhs.link).append(ein, rhs.ein).append(vat, rhs.vat).append(tin, rhs.tin).append(companyName, rhs.companyName).append(contact, rhs.contact).append(url, rhs.url).append(registrationNumber, rhs.registrationNumber).append(address, rhs.address).isEquals();
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "description",
            "field",
            "validationType"
    })
    public static class Message {

        @JsonProperty("description")
        public String description;
        @JsonProperty("field")
        public String field;
        @JsonProperty("validationType")
        public String validationType;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseDetail {

        @JsonProperty("id")
        private String id;
        @JsonProperty("requestData")
        private RequestData requestData;
        @JsonProperty("errorData")
        private List<Object> errorData = null;
        @JsonProperty("matchResults")
        private MatchResults matchResults;
        @JsonProperty("linkDetails")
        private LinkDetails linkDetails;
        @JsonProperty("matchData")
        private List<MatchDatum> matchData = null;
        private final static long serialVersionUID = -1652433096562372123L;


        @Override
        public String toString() {
            return new ToStringBuilder(this).append("id", id).append("requestData", requestData).append("errorData", errorData).append("matchResults", matchResults).append("linkDetails", linkDetails).append("matchData", matchData).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(id).append(errorData).append(matchResults).append(linkDetails).append(matchData).append(requestData).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof ResponseDetail) == false) {
                return false;
            }
            ResponseDetail rhs = ((ResponseDetail) other);
            return new EqualsBuilder().append(id, rhs.id).append(errorData, rhs.errorData).append(matchResults, rhs.matchResults).append(linkDetails, rhs.linkDetails).append(matchData, rhs.matchData).append(requestData, rhs.requestData).isEquals();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MatchResults implements Serializable {

        @JsonProperty("matchStatus")
        private String matchStatus;
        @JsonProperty("matchScoreData")
        private MatchScoreData matchScoreData;
        @JsonProperty("matchResultsLink")
        private String matchResultsLink;
        private final static long serialVersionUID = -8739866978538445705L;

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("matchStatus", matchStatus).append("matchScoreData", matchScoreData).append("matchResultsLink", matchResultsLink).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(matchStatus).append(matchResultsLink).append(matchScoreData).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof MatchResults) == false) {
                return false;
            }
            MatchResults rhs = ((MatchResults) other);
            return new EqualsBuilder().append(matchStatus, rhs.matchStatus).append(matchResultsLink, rhs.matchResultsLink).append(matchScoreData, rhs.matchScoreData).isEquals();
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "address1",
            "address2",
            "address3",
            "address4",
            "city",
            "state",
            "country",
            "zip"
    })
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address implements Serializable {

        @JsonProperty("streetAddress")
        private String streetAddress;
        @JsonProperty("address1")
        private String address1;
        @JsonProperty("address2")
        private String address2;
        @JsonProperty("address3")
        private String address3;
        @JsonProperty("address4")
        private String address4;
        @JsonProperty("city")
        private String city;
        @JsonProperty("state")
        private String state;
        @JsonProperty("country")
        private String country;
        @JsonProperty("zip")
        private String zip;
        private final static long serialVersionUID = 2400981463341391736L;


        @Override
        public String toString() {
            return new ToStringBuilder(this).append("address1", address1).append("address2", address2).append("address3", address3).append("address4", address4).append("city", city).append("state", state).append("country", country).append("zip", zip).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(zip).append(address4).append(state).append(address1).append(address2).append(address3).append(country).append(city).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof Address) == false) {
                return false;
            }
            Address rhs = ((Address) other);
            return new EqualsBuilder().append(zip, rhs.zip).append(address4, rhs.address4).append(state, rhs.state).append(address1, rhs.address1).append(address2, rhs.address2).append(address3, rhs.address3).append(country, rhs.country).append(city, rhs.city).isEquals();
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "trackId",
            "compliance"
    })
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Link implements Serializable {

        @JsonProperty("trackId")
        private String trackId;
        @JsonProperty("compliance")
        private List<Compliance> compliance = null;
        private final static long serialVersionUID = -887464360252603996L;


        @Override
        public String toString() {
            return new ToStringBuilder(this).append("trackId", trackId).append("compliance", compliance).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(trackId).append(compliance).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof Link) == false) {
                return false;
            }
            Link rhs = ((Link) other);
            return new EqualsBuilder().append(trackId, rhs.trackId).append(compliance, rhs.compliance).isEquals();
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "registeredBusinessData",
            "creditRiskData",
            "revenueData",
            "corporateHierarchyData",
            "verificationData",
            "executivesData",
            "complianceData"
    })
    @Getter
    @Setter
    public static class MatchDatum implements Serializable {

        @JsonProperty("registeredBusinessData")
        private TrackResponseModel.RegisteredBusinessData registeredBusinessData;
        @JsonProperty("creditRiskData")
        private CreditRiskData creditRiskData;
        @JsonProperty("revenueData")
        private RevenueData revenueData;
        @JsonProperty("corporateHierarchyData")
        private CorporateHierarchyData corporateHierarchyData;
        @JsonProperty("verificationData")
        private VerificationData verificationData;
        @JsonProperty("executivesData")
        private ExecutivesData executivesData;
        @JsonProperty("complianceData")
        private ComplianceData complianceData;
        private final static long serialVersionUID = 1740614548591164497L;

        /**
         * No args constructor for use in serialization
         */
        public MatchDatum() {
        }

        /**
         * @param corporateHierarchyData
         * @param registeredBusinessData
         * @param complianceData
         * @param executivesData
         * @param verificationData
         * @param creditRiskData
         * @param revenueData
         */
        public MatchDatum(TrackResponseModel.RegisteredBusinessData registeredBusinessData, CreditRiskData creditRiskData, RevenueData revenueData, CorporateHierarchyData corporateHierarchyData, VerificationData verificationData, ExecutivesData executivesData, ComplianceData complianceData) {
            super();
            this.registeredBusinessData = registeredBusinessData;
            this.creditRiskData = creditRiskData;
            this.revenueData = revenueData;
            this.corporateHierarchyData = corporateHierarchyData;
            this.verificationData = verificationData;
            this.executivesData = executivesData;
            this.complianceData = complianceData;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("registeredBusinessData", registeredBusinessData).append("creditRiskData", creditRiskData).append("revenueData", revenueData).append("corporateHierarchyData", corporateHierarchyData).append("verificationData", verificationData).append("executivesData", executivesData).append("complianceData", complianceData).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(corporateHierarchyData).append(registeredBusinessData).append(complianceData).append(executivesData).append(verificationData).append(creditRiskData).append(revenueData).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof MatchDatum) == false) {
                return false;
            }
            MatchDatum rhs = ((MatchDatum) other);
            return new EqualsBuilder().append(corporateHierarchyData, rhs.corporateHierarchyData).append(registeredBusinessData, rhs.registeredBusinessData).append(complianceData, rhs.complianceData).append(executivesData, rhs.executivesData).append(verificationData, rhs.verificationData).append(creditRiskData, rhs.creditRiskData).append(revenueData, rhs.revenueData).isEquals();
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "matchPercentage"
    })
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MatchScoreData implements Serializable {

        @JsonProperty("matchPercentage")
        private Double matchPercentage;
        private final static long serialVersionUID = -4439745066501635988L;


        @Override
        public String toString() {
            return new ToStringBuilder(this).append("matchPercentage", matchPercentage).toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(matchPercentage).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof MatchScoreData) == false) {
                return false;
            }
            MatchScoreData rhs = ((MatchScoreData) other);
            return new EqualsBuilder().append(matchPercentage, rhs.matchPercentage).isEquals();
        }

    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "verificationDataFlag",
        "verificationDetails"
})
@Getter
@Setter
class VerificationData implements Serializable {

    @JsonProperty("verificationDataFlag")
    private String verificationDataFlag;
    @JsonProperty("verificationDetails")
    private VerificationDetails verificationDetails;
    private final static long serialVersionUID = -500098682591572646L;

    /**
     * No args constructor for use in serialization
     */
    public VerificationData() {
    }

    /**
     * @param verificationDetails
     * @param verificationDataFlag
     */
    public VerificationData(String verificationDataFlag, VerificationDetails verificationDetails) {
        super();
        this.verificationDataFlag = verificationDataFlag;
        this.verificationDetails = verificationDetails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("verificationDataFlag", verificationDataFlag).append("verificationDetails", verificationDetails).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(verificationDetails).append(verificationDataFlag).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof VerificationData) == false) {
            return false;
        }
        VerificationData rhs = ((VerificationData) other);
        return new EqualsBuilder().append(verificationDetails, rhs.verificationDetails).append(verificationDataFlag, rhs.verificationDataFlag).isEquals();
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "verificationStatus",
        "verificationDate"
})
@Getter
@Setter
class VerificationDetails implements Serializable {

    @JsonProperty("verificationStatus")
    private Object verificationStatus;
    @JsonProperty("verificationDate")
    private Object verificationDate;
    private final static long serialVersionUID = 8319864474791102599L;

    /**
     * No args constructor for use in serialization
     */
    private VerificationDetails() {
    }

    /**
     * @param verificationDate
     * @param verificationStatus
     */
    private VerificationDetails(Object verificationStatus, Object verificationDate) {
        super();
        this.verificationStatus = verificationStatus;
        this.verificationDate = verificationDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("verificationStatus", verificationStatus).append("verificationDate", verificationDate).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(verificationDate).append(verificationStatus).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof VerificationDetails) == false) {
            return false;
        }
        VerificationDetails rhs = ((VerificationDetails) other);
        return new EqualsBuilder().append(verificationDate, rhs.verificationDate).append(verificationStatus, rhs.verificationStatus).isEquals();
    }


}



