package com.car.foryou.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ErrorDetails {
    private Date timestamp;
    private String error;
    private String message;
    private String details;
    private String path;
    private String exception;
}
