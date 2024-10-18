package com.car.foryou.service.auth;

import com.car.foryou.dto.auth.*;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.otp.OtpResponse;
import com.car.foryou.dto.otp.OtpType;
import com.car.foryou.dto.otp.OtpVerificationRequest;
import com.car.foryou.dto.refreshtoken.RefreshTokenRequest;
import com.car.foryou.dto.refreshtoken.RefreshTokenResponse;
import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import com.car.foryou.repository.group.GroupRepository;
import com.car.foryou.repository.user.UserRepository;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.otp.OtpService;
import com.car.foryou.mapper.UserMapper;
import com.car.foryou.service.refreshtoken.RefreshTokenServiceImpl;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

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
    private final NotificationService notificationService;



    public String register(UserRequest request){
        Group group = groupRepository.findByName(request.getGroup()).orElseThrow(
                () -> new RuntimeException("Groups with given name : '" +request.getGroup()+ "'")
        );
        userRepository.findByEmailOrUsernameOrPhoneNumber(
                        request.getEmail(), request.getUsername(), request.getPhoneNumber())
                .ifPresent(user -> {
                    throw new ResourceAlreadyExistsException("User", HttpStatus.CONFLICT);
                });
        User user = userMapper.mapToUser(request, group);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
//        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(save);
//        String accessToken = jwtService.generateToken(userInfoDetails, false);
//        String username = userInfoDetails.getUsername();
        String encodeToString = Base64.getEncoder().encodeToString(user.getEmail().getBytes(StandardCharsets.UTF_8));
        String verificationLink = "http://localhost:8080/auth/verify/"+encodeToString;
        MessageTemplate messageTemplate = MessageTemplate.builder()
                .name("emailVerification")
                .data(Map.of("verification_link", verificationLink))
                .build();
        notificationService.sendNotification(NotificationChannel.EMAIL,"Email Verification", messageTemplate, user.getEmail());

        return "User registered successfully, please check your email and verifying your email for further access";
    }

    public AuthResponse login(AuthLoginRequest request){
        User user = userRepository.findByUsername(request.getIdentifier()).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", request.getIdentifier())
        );
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );
        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(user);
        String accessToken = jwtService.generateToken(userInfoDetails, false);
        String refreshToken = null;
        String message = null;
        if (user.isVerified()){
            accessToken = jwtService.generateToken(userInfoDetails, true);
            refreshToken = refreshTokenServiceImpl.createRefreshToken(user.getUsername()).token();
        }

        if (user.isMfaEnabled()){
            accessToken = jwtService.generateToken(userInfoDetails, false);
            message = "MFA enabled, please request for OTP";
            refreshToken = null;
        }
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message(message)
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

    public String verifyEmail(String encodedEmail){
        String email = new String(Base64.getDecoder().decode(encodedEmail), StandardCharsets.UTF_8);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InvalidRequestException("Your given token is invalid", HttpStatus.BAD_REQUEST)
        );

        if (user.isVerified()){
            return "Email already verified, you can login now";
        }

        user.setVerified(true);
        userRepository.save(user);
        return "Email verified successfully, you can login now and enjoy our services";
    }

    public String enableMfa(){
        Integer id = CustomUserDetailService.getLoggedInUserDetails().getId();
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        if (user.getPhoneNumber() == null){
            return "Please add phone number first";
        }
        if (user.isMfaEnabled()){
            return "MFA already enabled";
        }
        OtpResponse otp = otpService.createOtp(
                OtpVerificationRequest.builder()
                        .otpType(OtpType.ENABLED_MFA)
                        .build()
        );
        MessageTemplate message = MessageTemplate.builder()
                .name("wa_otpRequest")
                .data(Map.of("otp", otp.getOtp()))
                .build();
        return notificationService.sendNotification(NotificationChannel.WHATSAPP, "OTP Verification", message, user.getPhoneNumber());
    }
}
