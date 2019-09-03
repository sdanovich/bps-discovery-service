package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Buyer Agent  Details
 */
@ApiModel(description = "Buyer Agent  Details")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-08-28T11:16:06.051-04:00")

public class BuyerAgent {
    @JsonProperty("bpsId")
    private String bpsId = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("address")
    private Address address = null;

    @JsonProperty("agentname")
    private String agentname = null;

    @JsonProperty("hooks")
    @Valid
    private List<Hook> hooks = null;

    public BuyerAgent bpsId(String bpsId) {
        this.bpsId = bpsId;
        return this;
    }

    /**
     * Get bpsId
     *
     * @return bpsId
     **/
    @ApiModelProperty(example = "fancy.nam.buy.bps", value = "")


    public String getBpsId() {
        return bpsId;
    }

    public void setBpsId(String bpsId) {
        this.bpsId = bpsId;
    }

    public BuyerAgent name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(example = "Fancy Shoes, Inc", value = "")


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BuyerAgent address(Address address) {
        this.address = address;
        return this;
    }

    /**
     * Get address
     *
     * @return address
     **/
    @ApiModelProperty(value = "")

    @Valid

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public BuyerAgent agentname(String agentname) {
        this.agentname = agentname;
        return this;
    }

    /**
     * Get agentname
     *
     * @return agentname
     **/
    @ApiModelProperty(example = "BuyerAgentName", value = "")


    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public BuyerAgent hooks(List<Hook> hooks) {
        this.hooks = hooks;
        return this;
    }

    public BuyerAgent addHooksItem(Hook hooksItem) {
        if (this.hooks == null) {
            this.hooks = new ArrayList<>();
        }
        this.hooks.add(hooksItem);
        return this;
    }

    /**
     * Get hooks
     *
     * @return hooks
     **/
    @ApiModelProperty(value = "")

    @Valid

    public List<Hook> getHooks() {
        return hooks;
    }

    public void setHooks(List<Hook> hooks) {
        this.hooks = hooks;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BuyerAgent buyerAgent = (BuyerAgent) o;
        return Objects.equals(this.bpsId, buyerAgent.bpsId) &&
                Objects.equals(this.name, buyerAgent.name) &&
                Objects.equals(this.address, buyerAgent.address) &&
                Objects.equals(this.agentname, buyerAgent.agentname) &&
                Objects.equals(this.hooks, buyerAgent.hooks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bpsId, name, address, agentname, hooks);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BuyerAgent {\n");

        sb.append("    bpsId: ").append(toIndentedString(bpsId)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    address: ").append(toIndentedString(address)).append("\n");
        sb.append("    agentname: ").append(toIndentedString(agentname)).append("\n");
        sb.append("    hooks: ").append(toIndentedString(hooks)).append("\n");
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

