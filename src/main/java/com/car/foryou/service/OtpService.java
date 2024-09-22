package com.car.foryou.service;

import com.car.foryou.dto.EmailVerifyingRequest;

public interface OtpService {
    String sendOtp(String email);
    String verifyMyEmailByOtp(EmailVerifyingRequest request);
}
