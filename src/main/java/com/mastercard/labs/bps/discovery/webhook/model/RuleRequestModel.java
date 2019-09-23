package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RuleRequestModel
 */
public class RuleRequestModel   {
    @JsonProperty("ruleId")
    private String ruleId = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("supplierProfileId")
    private String supplierProfileId = null;

    @JsonProperty("multiInvoice")
    private Boolean multiInvoice = null;

    @JsonProperty("amount")
    private Integer amount = null;

    @JsonProperty("numberOfDays")
    private Integer numberOfDays = null;

    /**
     * Rule rejects amount being greater or less than
     */
    public enum AmountOperatorEnum {
        OVER("OVER"),

        LESS("LESS"),

        EQUAL("EQUAL");

        private String value;

        AmountOperatorEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AmountOperatorEnum fromValue(String text) {
            for (AmountOperatorEnum b : AmountOperatorEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("amountOperator")
    private AmountOperatorEnum amountOperator = null;

    @JsonProperty("ccOnly")
    private Boolean ccOnly = null;

    /**
     * Rule status based on various criteria
     */
    public enum StatusEnum {
        ACTIVE("ACTIVE"),

        INACTIVE("INACTIVE"),

        ARCHIVED("ARCHIVED");

        private String value;

        StatusEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static StatusEnum fromValue(String text) {
            for (StatusEnum b : StatusEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("status")
    private StatusEnum status = StatusEnum.ACTIVE;

    @JsonProperty("dateActiveOn")
    private OffsetDateTime dateActiveOn = null;

    @JsonProperty("dateExpireOn")
    private LocalDate dateExpireOn = null;

    @JsonProperty("buyerTaxIds")
    private List<BuyerTaxId> buyerTaxIds = null;

    @JsonProperty("decisions")
    private List<DecisionPath> decisions = new ArrayList<>();

    public RuleRequestModel ruleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    /**
     * rule id
     * @return ruleId
     **/
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public RuleRequestModel name(String name) {
        this.name = name;
        return this;
    }

    /**
     * name of the rule
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleRequestModel supplierProfileId(String supplierProfileId) {
        this.supplierProfileId = supplierProfileId;
        return this;
    }

    /**
     * Either single or multi invoice restricted
     * @return supplierProfileId
     **/
    public String getSupplierProfileId() {
        return supplierProfileId;
    }

    public void setSupplierProfileId(String supplierProfileId) {
        this.supplierProfileId = supplierProfileId;
    }

    public RuleRequestModel multiInvoice(Boolean multiInvoice) {
        this.multiInvoice = multiInvoice;
        return this;
    }

    /**
     * Either single or multi invoice restricted
     * @return multiInvoice
     **/
    public Boolean isMultiInvoice() {
        return multiInvoice;
    }

    public void setMultiInvoice(Boolean multiInvoice) {
        this.multiInvoice = multiInvoice;
    }

    public RuleRequestModel amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Amount that is restricted
     * @return amount
     **/
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public RuleRequestModel numberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
        return this;
    }

    /**
     * Number of days after the invoice date the rule is valid
     * @return numberOfDays
     **/
    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public RuleRequestModel amountOperator(AmountOperatorEnum amountOperator) {
        this.amountOperator = amountOperator;
        return this;
    }

    /**
     * Rule rejects amount being greater or less than
     * @return amountOperator
     **/
    public AmountOperatorEnum getAmountOperator() {
        return amountOperator;
    }

    public void setAmountOperator(AmountOperatorEnum amountOperator) {
        this.amountOperator = amountOperator;
    }

    public RuleRequestModel ccOnly(Boolean ccOnly) {
        this.ccOnly = ccOnly;
        return this;
    }

    /**
     * are credit card only accepted
     * @return ccOnly
     **/
    public Boolean isCcOnly() {
        return ccOnly;
    }

    public void setCcOnly(Boolean ccOnly) {
        this.ccOnly = ccOnly;
    }

    public RuleRequestModel status(StatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * Rule status based on various criteria
     * @return status
     **/
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public RuleRequestModel dateActiveOn(OffsetDateTime dateActiveOn) {
        this.dateActiveOn = dateActiveOn;
        return this;
    }

    /**
     * Get dateActiveOn
     * @return dateActiveOn
     **/
    public OffsetDateTime getDateActiveOn() {
        return dateActiveOn;
    }

    public void setDateActiveOn(OffsetDateTime dateActiveOn) {
        this.dateActiveOn = dateActiveOn;
    }

    public RuleRequestModel dateExpireOn(LocalDate dateExpireOn) {
        this.dateExpireOn = dateExpireOn;
        return this;
    }

    /**
     * Get dateExpireOn
     * @return dateExpireOn
     **/
    public LocalDate getDateExpireOn() {
        return dateExpireOn;
    }

    public void setDateExpireOn(LocalDate dateExpireOn) {
        this.dateExpireOn = dateExpireOn;
    }

    public RuleRequestModel buyerTaxIds(List<BuyerTaxId> buyerTaxIds) {
        this.buyerTaxIds = buyerTaxIds;
        return this;
    }

    public RuleRequestModel addBuyerTaxIdsItem(BuyerTaxId buyerTaxIdsItem) {
        if (this.buyerTaxIds == null) {
            this.buyerTaxIds = new ArrayList<>();
        }
        this.buyerTaxIds.add(buyerTaxIdsItem);
        return this;
    }

    public List<BuyerTaxId> getBuyerTaxIds() {
        return buyerTaxIds;
    }

    public void setBuyerTaxIds(List<BuyerTaxId> buyerTaxIds) {
        this.buyerTaxIds = buyerTaxIds;
    }

    public RuleRequestModel decisions(List<DecisionPath> decisions) {
        this.decisions = decisions;
        return this;
    }

    public RuleRequestModel addDecisionsItem(DecisionPath decisionsItem) {
        this.decisions.add(decisionsItem);
        return this;
    }

    /**
     * decision path
     * @return decisions
     **/
    public List<DecisionPath> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<DecisionPath> decisions) {
        this.decisions = decisions;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RuleRequestModel {\n");

        sb.append("    ruleId: ").append(toIndentedString(ruleId)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    supplierProfileId: ").append(toIndentedString(supplierProfileId)).append("\n");
        sb.append("    multiInvoice: ").append(toIndentedString(multiInvoice)).append("\n");
        sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
        sb.append("    numberOfDays: ").append(toIndentedString(numberOfDays)).append("\n");
        sb.append("    amountOperator: ").append(toIndentedString(amountOperator)).append("\n");
        sb.append("    ccOnly: ").append(toIndentedString(ccOnly)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    dateActiveOn: ").append(toIndentedString(dateActiveOn)).append("\n");
        sb.append("    dateExpireOn: ").append(toIndentedString(dateExpireOn)).append("\n");
        sb.append("    buyerTaxIds: ").append(toIndentedString(buyerTaxIds)).append("\n");
        sb.append("    decisions: ").append(toIndentedString(decisions)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

