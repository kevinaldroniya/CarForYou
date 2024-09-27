package com.car.foryou.service.impl;

import com.car.foryou.dto.auth.*;
import com.car.foryou.model.RefreshToken;
import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import com.car.foryou.repository.GroupRepository;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.OtpService;
import com.car.foryou.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final GroupRepository groupRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;


    public AuthResponse register(UserRequest request){
        Group group = groupRepository.findByName(request.getGroup()).orElseThrow(
                () -> new RuntimeException("Groups with given name : '" +request.getGroup()+ "'")
        );
        userRepository.findByEmailOrUsernameOrPhoneNumber(
                        request.getEmail(), request.getUsername(), request.getPhoneNumber())
                .ifPresent(user -> {
                    throw new RuntimeException("User already exists");
                });
        User user = userMapper.mapToUser(request, group);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User save = userRepository.save(user);
        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(save);
        String accessToken = jwtService.generateToken(userInfoDetails, true);
//        OtpVerificationRequest otpVerificationRequest = OtpVerificationRequest.builder()
//                .email(user.getEmail())
//                .otpType(OtpType.REGISTER)
//                .build();
//        otpService.sendOtp(otpVerificationRequest);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );
        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(user);
        String accessToken = jwtService.generateToken(userInfoDetails, false);
//        OtpVerificationRequest otpVerificationRequest = OtpVerificationRequest.builder()
//                .email(user.getEmail())
//                .otpType(OtpType.LOGIN)
//                .build();
//        otpService.sendOtp(otpVerificationRequest);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .message("Login Successful, please check your email and verifying your email by otp code for further access")
                .build();
    }

    public AuthResponse getNewAccessToken(RefreshTokenRequest request){
        RefreshTokenResponse refreshToken = refreshTokenServiceImpl.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.user();
        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(user);
        String accessToken = jwtService.generateToken(userInfoDetails, true);
        return AuthResponse.builder()
                .refreshToken(refreshToken.token())
                .accessToken(accessToken)
                .build();
    }
}
