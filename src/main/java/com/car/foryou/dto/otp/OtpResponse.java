package com.car.foryou.dto.otp;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class OtpResponse {
    private Integer otp;
    private OtpType otpType;
    private String message;
    private ZonedDateTime timeExpiration;
}
