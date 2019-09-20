package com.mastercard.labs.bps.discovery.webhook.model.ui;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(value = {"fileName", "type", "uploadDate", "status"}, alphabetic = false)
public class DiscoveryTable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("fileName")
    private String fileName;
    @JsonIgnore
    private LocalDateTime uploadDate;
    @JsonIgnore
    private Integer timeZone;
    @JsonIgnore
    private BatchFile.STATUS status;
    @JsonIgnore
    private BatchFile.TYPE type;
    @JsonIgnore
    private BatchFile.ENTITY entity;
    @JsonIgnore
    private int completePercent;

    @JsonGetter("uploadDate")
    public String getUploadDate() {
        return uploadDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm a"));
    }

    @JsonGetter("action")
    public String getAction() {
        String output;
        String operation = null;
        String entity = null;
        if (status == BatchFile.STATUS.COMPLETE) {
            if (this.entity == BatchFile.ENTITY.BUYER) {
                entity = "buyers";
            } else if (this.entity == BatchFile.ENTITY.SUPPLIER) {
                entity = "suppliers";
            }

            if (type == BatchFile.TYPE.LOOKUP) {
                operation = "discovery";
            } else if (type == BatchFile.TYPE.REGISTRATION) {
                operation = "registration";
            }
            output = "/" + Stream.of(operation, entity, id).collect(Collectors.joining("/"));
        } else {
            output = "";
        }
        return output;
    }

    @JsonGetter("status")
    public String getStatus() {
        if (status == BatchFile.STATUS.PROCESSING && completePercent != 0) {
            return status.name() + " (" + completePercent + "%)";
        } else {
            return status.name();
        }
    }

    @JsonGetter("type")
    public String getType() {
        String operation = null;
        String entity = null;

        if (this.entity == BatchFile.ENTITY.BUYER) {
            entity = "Buyer";
        } else if (this.entity == BatchFile.ENTITY.SUPPLIER) {
            entity = "Supplier";
        }

        if (type == BatchFile.TYPE.LOOKUP) {
            operation = "Discovery";
        } else if (type == BatchFile.TYPE.REGISTRATION) {
            operation = "Registration";
        }
        return Stream.of(entity, operation).collect(Collectors.joining(" "));
    }
}
