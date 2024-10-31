package com.car.foryou.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponse<T> {
    private String message;
    private T data;
    private ZonedDateTime timestamp;
}
