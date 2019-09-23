package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RuleFullResponseRules
 */
public class RuleFullResponseRules implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("decisions")
  private List<DecisionPath> decisions = null;

  public RuleFullResponseRules name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  **/
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RuleFullResponseRules decisions(List<DecisionPath> decisions) {
    this.decisions = decisions;
    return this;
  }

  public RuleFullResponseRules addDecisionsItem(DecisionPath decisionsItem) {
    if (this.decisions == null) {
      this.decisions = new ArrayList<>();
    }
    this.decisions.add(decisionsItem);
    return this;
  }

  /**
   * Get decisions
   * @return decisions
  **/
  public List<DecisionPath> getDecisions() {
    return decisions;
  }

  public void setDecisions(List<DecisionPath> decisions) {
    this.decisions = decisions;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleFullResponseRules ruleFullResponseRules = (RuleFullResponseRules) o;
    return Objects.equals(this.name, ruleFullResponseRules.name) &&
        Objects.equals(this.decisions, ruleFullResponseRules.decisions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, decisions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuleFullResponseRules {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    decisions: ").append(toIndentedString(decisions)).append("\n");
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

