package com.car.foryou.dto.email;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class EmailVerificationDto {
    private String email;
    private Integer otp;
    private ZonedDateTime timeExpiration;
    private ZonedDateTime createdAt;
}
