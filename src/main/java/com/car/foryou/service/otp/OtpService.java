package com.car.foryou.service.otp;

import com.car.foryou.dto.otp.OtpValidationRequest;
import com.car.foryou.dto.otp.OtpVerificationRequest;

public interface OtpService {
    String sendOtp(OtpVerificationRequest otpVerificationRequest);
    String verifyOtp(String authHeader, OtpValidationRequest otpValidationRequest);
}
