package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RuleFullResponse
 */
public class RuleFullResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("rules")
  private List<RuleFullResponseRules> rules = null;

  public RuleFullResponse rules(List<RuleFullResponseRules> rules) {
    this.rules = rules;
    return this;
  }

  public RuleFullResponse addRulesItem(RuleFullResponseRules rulesItem) {
    if (this.rules == null) {
      this.rules = new ArrayList<>();
    }
    this.rules.add(rulesItem);
    return this;
  }

  /**
   * Get rules
   * @return rules
  **/

  public List<RuleFullResponseRules> getRules() {
    return rules;
  }

  public void setRules(List<RuleFullResponseRules> rules) {
    this.rules = rules;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleFullResponse ruleFullResponse = (RuleFullResponse) o;
    return Objects.equals(this.rules, ruleFullResponse.rules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rules);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuleFullResponse {\n");

    sb.append("    rules: ").append(toIndentedString(rules)).append("\n");
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

