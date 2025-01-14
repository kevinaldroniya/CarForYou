package com.car.foryou.controller;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auth.AuthResetPassword;
import com.car.foryou.dto.auth.ForgotPasswordRequest;
import com.car.foryou.dto.email.EmailVerifRequest;
import com.car.foryou.service.auth.AuthService;
import com.car.foryou.dto.auth.AuthResponse;
import com.car.foryou.dto.auth.AuthLoginRequest;
import com.car.foryou.dto.refreshtoken.RefreshTokenRequest;
import com.car.foryou.dto.user.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController  {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<String>> register(@RequestBody UserRequest userRequest){
        return new ResponseEntity<>(authService.register(userRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest request){
        AuthResponse login = authService.login(request);
        return new ResponseEntity<>(login, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request){
        AuthResponse authResponse = authService.getNewAccessToken(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> verifyEmail(@RequestParam(value = "signature", required = true) String signature){
        return new ResponseEntity<>(authService.verifyEmail(signature), HttpStatus.OK);
    }

    @GetMapping("/enableMfa")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> enableMfa(){
        return new ResponseEntity<>(authService.enableMfa(), HttpStatus.OK);
    }

    @PostMapping(
            path = "/request/emailVerification"
    )
    public ResponseEntity<GeneralResponse<Map<String, Object>>> requestEmailVerification(@RequestBody EmailVerifRequest request){
        return ResponseEntity.ok(authService.requestEmailVerification(request.email()));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> forgotPassword(@RequestBody ForgotPasswordRequest request){
        return ResponseEntity.ok(authService.resetPasswordRequest(request.email()));
    }

    @GetMapping("/forgotPassword/verify")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> verifyForgotPasswordRequest(@RequestParam (name = "signature", required = true) String signature) {
        return ResponseEntity.ok(authService.forgotPasswordVerify(signature));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestHeader (name = "signature") String signature,
                                                @RequestHeader (name = "otp") Integer otp,
                                                @RequestHeader (name = "userId") Integer userId,
                                                @RequestBody AuthResetPassword resetPassword) {
        return ResponseEntity.ok(authService.resetPassword(signature, otp, userId, resetPassword));
    }
}
