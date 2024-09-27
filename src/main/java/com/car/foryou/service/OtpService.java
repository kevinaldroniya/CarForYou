package com.car.foryou.service;

import com.car.foryou.dto.auth.OtpValidationRequest;
import com.car.foryou.dto.auth.OtpVerificationRequest;

public interface OtpService {
    String sendOtp(OtpVerificationRequest otpVerificationRequest);
    String verifyOtp(String authHeader, OtpValidationRequest otpValidationRequest);
}
