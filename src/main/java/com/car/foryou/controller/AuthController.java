package com.car.foryou.controller;

import com.car.foryou.api.v1.BaseApiControllerV1;
import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.email.EmailVerificationRequest;
import com.car.foryou.service.auth.AuthService;
import com.car.foryou.dto.auth.AuthResponse;
import com.car.foryou.dto.auth.AuthLoginRequest;
import com.car.foryou.dto.refreshtoken.RefreshTokenRequest;
import com.car.foryou.dto.user.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController implements BaseApiControllerV1 {

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
    public ResponseEntity<GeneralResponse<String>> verifyEmail(@RequestParam(value = "signature", required = true) String signature){
        return new ResponseEntity<>(authService.verifyEmail(signature), HttpStatus.OK);
    }

    @GetMapping("/enableMfa")
    public ResponseEntity<GeneralResponse<String>> enableMfa(){
        return new ResponseEntity<>(authService.enableMfa(), HttpStatus.OK);
    }

    @PostMapping("/request/emailVerification")
    public ResponseEntity<GeneralResponse<String>> requestEmailVerification(@RequestBody EmailVerificationRequest request){
        return ResponseEntity.ok(authService.requestEmailVerification(request.getEmail()));
    }
}
