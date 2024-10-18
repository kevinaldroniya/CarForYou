package com.car.foryou.dto.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OtpVerifyResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
}
