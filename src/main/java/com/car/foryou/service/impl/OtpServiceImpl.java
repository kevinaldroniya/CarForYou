package com.car.foryou.service.impl;

import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.auth.OtpValidationRequest;
import com.car.foryou.dto.auth.MailBody;
import com.car.foryou.mapper.UserMapper;
import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import com.car.foryou.repository.OtpRepository;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.EmailService;
import com.car.foryou.service.OtpService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService, JwtService jwtService, UserMapper userMapper) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public String sendOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User with given email : " + email + ", not found")
        );

        int generatedOtp = otpGenerator();
        Otp otp = Otp.builder()
                .otpNumber(generatedOtp)
                .otpExpiration(ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(60).toEpochSecond())
                .user(user)
                .build();
        otpRepository.save(otp);

        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .subject("Email Verification")
                .text("Your OTP is : " + generatedOtp)
                .build();

        emailService.sendSimpleMessage(mailBody);
        return "Otp sent successfully, please check your email";
    }

    private int otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }

    @Override
    public String verifyMyEmailByOtp(String authToken, OtpValidationRequest otpValidationRequest) {

        String tempJwt = authToken.substring(7);
        String username = jwtService.extractUsername(tempJwt);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Otp otp = otpRepository.findByOtpNumberAndUser(otpValidationRequest.getOtp(), user).orElseThrow(
                () -> new RuntimeException("Invalid OTP")
        );

        if (otp.getOtpExpiration() < ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()) {
            throw new RuntimeException("OTP expired");
        }

        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(user);

//        otpRepository.delete(otp);
        // Generate a new JWT token with MFA authenticated flag
        return jwtService.generateToken(userInfoDetails, true);
    }

}
