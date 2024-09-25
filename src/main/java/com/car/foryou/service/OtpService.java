package com.car.foryou.service;

import com.car.foryou.dto.auth.OtpValidationRequest;

public interface OtpService {
    String sendOtp(String email);
    String verifyMyEmailByOtp(String authToken, OtpValidationRequest otpValidationRequest);
}
