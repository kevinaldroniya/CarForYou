package com.car.foryou.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ErrorDetails {
    private Date timestamp;
    private int error;
    private String message;
    private String details;
    private String path;
}
