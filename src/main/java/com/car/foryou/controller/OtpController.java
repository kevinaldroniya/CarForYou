package com.car.foryou.controller;

import com.car.foryou.dto.EmailRequestOtp;
import com.car.foryou.dto.EmailVerifyingRequest;
import com.car.foryou.service.EmailService;
import com.car.foryou.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
public class OtpController {

    private final EmailService emailService;
    private final OtpService otpService;

    public OtpController(EmailService emailService, OtpService otpService) {
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @PostMapping(
            path = "/verifyMyEmail",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> verifyEmail(HttpServletRequest request, @RequestBody EmailVerifyingRequest verifyingRequest){
        String authToken = request.getHeader("Authorization");
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }
        String response = otpService.verifyMyEmailByOtp(authToken, verifyingRequest);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
    }

    @PostMapping(
            path = "/sendOtp",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> sendOtp(@RequestBody EmailRequestOtp emailRequestOtp){
        String response = otpService.sendOtp(emailRequestOtp.getEmail());
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }
}
