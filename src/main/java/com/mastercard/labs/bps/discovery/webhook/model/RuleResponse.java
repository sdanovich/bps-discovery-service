package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * RuleResponse
 */
public class RuleResponse  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("supplierId")
  private String supplierId = null;

  @JsonProperty("decision")
  private RuleFullResponse decision;

  @JsonProperty("description")
  private String description = null;

  public RuleResponse supplierId(String supplierId) {
    this.supplierId = supplierId;
    return this;
  }

  /**
   * Get supplierId
   * @return supplierId
  **/
  @NotNull

  public String getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(String supplierId) {
    this.supplierId = supplierId;
  }


  public RuleResponse decision(RuleFullResponse decision) {
    this.decision = decision;
    return this;
  }

  /**
   * Get decision
   * @return decision
   **/
  public RuleFullResponse getDecision() {
    return decision;
  }

  public RuleResponse description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Property used for error descriptions
   * @return description
  **/
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleResponse ruleResponse = (RuleResponse) o;
    return Objects.equals(this.supplierId, ruleResponse.supplierId) &&
        Objects.equals(this.decision, ruleResponse.decision) &&
        Objects.equals(this.description, ruleResponse.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(supplierId, decision, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuleResponse {\n");

    sb.append("    supplierId: ").append(toIndentedString(supplierId)).append("\n");
    sb.append("    decision: ").append(toIndentedString(decision)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

