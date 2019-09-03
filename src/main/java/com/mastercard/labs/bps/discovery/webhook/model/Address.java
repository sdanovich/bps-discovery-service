package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Address
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-08-28T11:16:06.051-04:00")

public class Address {
    @JsonProperty("street")
    private String street = null;

    @JsonProperty("city")
    private String city = null;

    @JsonProperty("state")
    private String state = null;

    @JsonProperty("zip")
    private String zip = null;

    @JsonProperty("country")
    private String country = null;

    public Address street(String street) {
        this.street = street;
        return this;
    }

    /**
     * 317 Madison Ave. Ste 900
     *
     * @return street
     **/
    @ApiModelProperty(value = "317 Madison Ave. Ste 900")


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Address city(String city) {
        this.city = city;
        return this;
    }

    /**
     * New York
     *
     * @return city
     **/
    @ApiModelProperty(value = "New York")


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Address state(String state) {
        this.state = state;
        return this;
    }

    /**
     * NY
     *
     * @return state
     **/
    @ApiModelProperty(value = "NY")


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Address zip(String zip) {
        this.zip = zip;
        return this;
    }

    /**
     * 01198-0198
     *
     * @return zip
     **/
    @ApiModelProperty(value = "01198-0198")


    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Address country(String country) {
        this.country = country;
        return this;
    }

    /**
     * USA
     *
     * @return country
     **/
    @ApiModelProperty(value = "USA")


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(this.street, address.street) &&
                Objects.equals(this.city, address.city) &&
                Objects.equals(this.state, address.state) &&
                Objects.equals(this.zip, address.zip) &&
                Objects.equals(this.country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, zip, country);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Address {\n");

        sb.append("    street: ").append(toIndentedString(street)).append("\n");
        sb.append("    city: ").append(toIndentedString(city)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    zip: ").append(toIndentedString(zip)).append("\n");
        sb.append("    country: ").append(toIndentedString(country)).append("\n");
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

