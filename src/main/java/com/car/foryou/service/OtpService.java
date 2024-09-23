package com.car.foryou.service;

import com.car.foryou.dto.EmailVerifyingRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface OtpService {
    String sendOtp(String email);
    String verifyMyEmailByOtp(String authToken, EmailVerifyingRequest emailVerifyingRequest);
}
