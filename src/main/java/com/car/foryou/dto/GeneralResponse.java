package com.car.foryou.dto;

import com.car.foryou.dto.error.ErrorDetails;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponse<T> {
//    private String status;
    private String message;
    private T data;
    private ZonedDateTime timestamp;
}
