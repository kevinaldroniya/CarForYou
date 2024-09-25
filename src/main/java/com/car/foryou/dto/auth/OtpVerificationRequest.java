package com.car.foryou.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpVerificationRequest {
    private String email;
}
