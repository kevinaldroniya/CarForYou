package com.car.foryou.controller;

import com.car.foryou.dto.auth.OtpValidationRequest;
import com.car.foryou.dto.auth.OtpVerificationRequest;
import com.car.foryou.service.OtpService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping(
            path = "/verifyOtp",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> verifyOtp(@RequestHeader("Authorization") String authHeader, @RequestBody OtpValidationRequest verifyingRequest){
        String response = otpService.verifyOtp(authHeader, verifyingRequest);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
    }

    @PostMapping(
            path = "/sendOtp",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> sendOtp(@RequestBody OtpVerificationRequest otpVerificationRequest){
        String response = otpService.sendOtp(otpVerificationRequest);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
    }

}
