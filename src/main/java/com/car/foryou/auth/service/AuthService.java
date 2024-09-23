package com.car.foryou.auth.service;

import com.car.foryou.auth.entity.RefreshToken;
import com.car.foryou.auth.util.AuthResponse;
import com.car.foryou.auth.util.LoginRequest;
import com.car.foryou.auth.util.RefreshTokenRequest;
import com.car.foryou.auth.util.UserInfoDetails;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import com.car.foryou.repository.GroupRepository;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.OtpService;
import com.car.foryou.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
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
        UserInfoDetails userInfoDetails = mapToUserDetails(save);
        String accessToken = jwtService.generateToken(userInfoDetails, false);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(save.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
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
        UserInfoDetails userInfoDetails = mapToUserDetails(user);
        String accessToken = jwtService.generateToken(userInfoDetails, false);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());

        otpService.sendOtp(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message("Login Successful, please check your email and verifying your email by otp code for further access")
                .build();
    }

    public AuthResponse verifyToken(RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();
        UserInfoDetails userInfoDetails = mapToUserDetails(user);
        String accessToken = jwtService.generateToken(userInfoDetails, false);
        return AuthResponse.builder()
                .refreshToken(refreshToken.getToken())
                .accessToken(accessToken)
                .build();
    }

    private UserInfoDetails mapToUserDetails(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getGroup().getName()));
        return UserInfoDetails.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}