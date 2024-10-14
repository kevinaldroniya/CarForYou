package com.car.foryou.dto.otp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpVerificationRequest {
    private String email;
    private OtpType otpType;
}
