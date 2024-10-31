package com.car.foryou.service.otp;

import com.car.foryou.dto.otp.*;
import com.car.foryou.model.Otp;

public interface OtpService {
    OtpResponse createOtp(OtpVerificationRequest otpVerificationRequest);
    OtpVerifyResponse verifyOtp(OtpValidationRequest otpValidationRequest);
    String verifyEmailOtp(String email, int otp);
    OtpResponse generateOtp(String email, OtpType otpType);
    void unSignOtpVerify(Integer otp, String email);
    Otp getOtpByUserAndOtpType(String email, OtpType otpType);
    void deleteOtp(Integer otp);
}
