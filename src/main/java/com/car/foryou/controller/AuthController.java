package com.car.foryou.controller;

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
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest userRequest){
        String register = authService.register(userRequest);
        return new ResponseEntity<>(register, HttpStatus.CREATED);
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

    @GetMapping("/verify/{encodeEmail}")
    public ResponseEntity<String> verifyEmail(@PathVariable("encodeEmail") String email){
        String response = authService.verifyEmail(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/enableMfa")
    public ResponseEntity<String> enableMfa(){
        String response = authService.enableMfa();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
