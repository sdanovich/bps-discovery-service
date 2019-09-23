package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * DecisionPath
 */

public class DecisionPath   {
    /**
     * Gets or Sets decisionCode
     */
    public enum DecisionCodeEnum {
        WARNING("WARNING"),

        ERROR("ERROR"),

        REJECT("REJECT");

        private String value;

        DecisionCodeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static DecisionCodeEnum fromValue(String text) {
            for (DecisionCodeEnum b : DecisionCodeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("decisionCode")
    private DecisionCodeEnum decisionCode = null;

    @JsonProperty("decisionDescription")
    private String decisionDescription = null;

    public DecisionPath decisionCode(DecisionCodeEnum decisionCode) {
        this.decisionCode = decisionCode;
        return this;
    }

    /**
     * Get decisionCode
     * @return decisionCode
     **/
    public DecisionCodeEnum getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(DecisionCodeEnum decisionCode) {
        this.decisionCode = decisionCode;
    }

    public DecisionPath decisionDescription(String decisionDescription) {
        this.decisionDescription = decisionDescription;
        return this;
    }

    /**
     * Get decisionDescription
     * @return decisionDescription
     **/
    public String getDecisionDescription() {
        return decisionDescription;
    }

    public void setDecisionDescription(String decisionDescription) {
        this.decisionDescription = decisionDescription;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DecisionPath {\n");

        sb.append("    decisionCode: ").append(toIndentedString(decisionCode)).append("\n");
        sb.append("    decisionDescription: ").append(toIndentedString(decisionDescription)).append("\n");
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

