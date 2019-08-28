package com.mastercard.labs.bps.discovery.webhook.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HealthCheck implements Serializable {
    private Object status;
    Details details;
}

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
class Details {
    private Long total;
    private Long free;
    private Long threshold;
    Db db;
    DiskSpace diskSpace;
    private String database;
    private Integer hello;
}

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
class Db {
    private String status;
    Details details;
}

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
class DiskSpace {
    private String status;
    Details details;
}
