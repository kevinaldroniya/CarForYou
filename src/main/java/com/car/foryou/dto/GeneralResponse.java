package com.car.foryou.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class GeneralResponse<T> {
    private String message;
    private T data;
    private ZonedDateTime timestamp;
}
