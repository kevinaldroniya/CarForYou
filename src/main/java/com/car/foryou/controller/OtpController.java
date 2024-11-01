package com.car.foryou.controller;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.otp.OtpResponse;
import com.car.foryou.dto.otp.OtpValidationRequest;
import com.car.foryou.dto.otp.OtpVerificationRequest;
import com.car.foryou.dto.otp.OtpVerifyResponse;
import com.car.foryou.service.otp.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;


@RestController
@RequestMapping("/otp")
public class OtpController extends BaseApiControllerV1 {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping(
            path = "/verifyOtp",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OtpVerifyResponse> verifyOtp(@RequestBody OtpValidationRequest verifyingRequest){
        OtpVerifyResponse otpVerifyResponse = otpService.verifyOtp(verifyingRequest);
        return new ResponseEntity<>(otpVerifyResponse, org.springframework.http.HttpStatus.OK);
    }

    @PostMapping(
            path = "/otpRequest",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GeneralResponse<String>> sendOtp(@RequestBody OtpVerificationRequest otpVerificationRequest){
        OtpResponse otp = otpService.createOtp(otpVerificationRequest);
        GeneralResponse<String> responseBuilder = GeneralResponse.<String>builder()
                .message(otp.getMessage())
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();
        return new ResponseEntity<>(responseBuilder, HttpStatus.OK);
    }

}
