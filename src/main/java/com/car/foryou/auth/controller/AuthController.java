package com.car.foryou.auth.controller;

import com.car.foryou.auth.entity.RefreshToken;
import com.car.foryou.auth.service.AuthService;
import com.car.foryou.auth.service.RefreshTokenService;
import com.car.foryou.auth.util.AuthResponse;
import com.car.foryou.auth.util.LoginRequest;
import com.car.foryou.auth.util.RefreshTokenRequest;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRequest userRequest){
        AuthResponse register = authService.register(userRequest);
        return new ResponseEntity<>(register, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        AuthResponse login = authService.login(request);
        return new ResponseEntity<>(login, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request){
        AuthResponse authResponse = authService.verifyToken(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
