package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * To include buyerTaxIds in the rule
 */
public class BuyerTaxId   {
    @JsonProperty("buyerTaxId")
    private String buyerTaxId = null;

    /**
     * Gets or Sets operation
     */
    public enum OperationEnum {
        INCLUDED("INCLUDED"),

        EXCLUDED("EXCLUDED");

        private String value;

        OperationEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static OperationEnum fromValue(String text) {
            for (OperationEnum b : OperationEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("operation")
    private OperationEnum operation = null;

    public BuyerTaxId buyerTaxId(String buyerTaxId) {
        this.buyerTaxId = buyerTaxId;
        return this;
    }

    /**
     * buyer tax Id that will be included or excluded in a rule
     * @return buyerTaxId
     **/
    public String getBuyerTaxId() {
        return buyerTaxId;
    }

    public void setBuyerTaxId(String buyerTaxId) {
        this.buyerTaxId = buyerTaxId;
    }

    public BuyerTaxId operation(OperationEnum operation) {
        this.operation = operation;
        return this;
    }

    /**
     * Get operation
     * @return operation
     **/
    public OperationEnum getOperation() {
        return operation;
    }

    public void setOperation(OperationEnum operation) {
        this.operation = operation;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BuyerTaxId {\n");

        sb.append("    buyerTaxId: ").append(toIndentedString(buyerTaxId)).append("\n");
        sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
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

