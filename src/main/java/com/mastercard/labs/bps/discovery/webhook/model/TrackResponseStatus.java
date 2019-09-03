package com.mastercard.labs.bps.discovery.webhook.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "result",
        "statusCode",
        "status"
})
@Setter
@Getter
public class TrackResponseStatus {

    @JsonProperty("result")
    private String result;
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("status")
    private HttpStatus status;


}
