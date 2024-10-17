package com.car.foryou.service.otp;

import com.car.foryou.dto.otp.OtpValidationRequest;
import com.car.foryou.dto.otp.OtpVerificationRequest;

public interface OtpService {
    Integer createOtp(OtpVerificationRequest otpVerificationRequest);
    String verifyOtp(OtpValidationRequest otpValidationRequest);
    String verifyEmailOtp(String email, int otp);
}
