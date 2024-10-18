package com.car.foryou.service.otp;

import com.car.foryou.dto.otp.OtpResponse;
import com.car.foryou.dto.otp.OtpValidationRequest;
import com.car.foryou.dto.otp.OtpVerificationRequest;
import com.car.foryou.dto.otp.OtpVerifyResponse;

public interface OtpService {
    OtpResponse createOtp(OtpVerificationRequest otpVerificationRequest);
    OtpVerifyResponse verifyOtp(OtpValidationRequest otpValidationRequest);
    String verifyEmailOtp(String email, int otp);
}
