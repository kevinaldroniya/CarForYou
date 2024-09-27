package com.car.foryou.service.impl;

import com.car.foryou.dto.auth.OtpVerificationRequest;
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
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
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
    private final SecureRandom random;
    private static final int MAX_OTP_REQUEST = 5;


    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService, JwtService jwtService, UserMapper userMapper) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.random =  new SecureRandom();
    }

    @Override
    public String sendOtp(OtpVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new RuntimeException("User with given email : " + request.getEmail() + ", not found")
        );
        List<Otp> otpList = otpRepository.findAllByUserAndOtpType(user, request.getOtpType().name());
        otpLimitCheck(otpList);

        int generatedOtp = otpGenerator();

        Otp otp = Otp.builder()
                .otpNumber(generatedOtp)
                .otpExpiration(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(120).toEpochSecond())
                .user(user)
                .otpType(request.getOtpType().name())
                .build();
        otpRepository.save(otp);

        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .subject("Email Verification")
                .text(getEmailText(user, generatedOtp))
                .build();

        emailService.sendSimpleMessage(mailBody);
        return "Otp sent successfully, please check your email";
    }

    @Override
    public String verifyOtp(String authHeader, OtpValidationRequest otpValidationRequest) {
        String tempJwt = authHeader.substring(7);
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

        if (otp.getOtpType().equals("REGISTER")) {
            user.setVerified(true);
            userRepository.save(user);
        }

        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(user);
        otpRepository.deleteAllByUserAndOtpType(user, otp.getOtpType());
        return jwtService.generateToken(userInfoDetails, true);
    }

    private void otpLimitCheck(List<Otp> otpList) {
        if (otpList.size() >= MAX_OTP_REQUEST){
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
            ZonedDateTime lastExpiration = ZonedDateTime.ofInstant(Instant.ofEpochSecond(otpList.get(otpList.size()-1).getOtpExpiration()), ZoneId.of("UTC"));
            if (now.isBefore(lastExpiration.plusHours(2))) {
                throw new RuntimeException("You have reached maximum OTP request, please try again later");
            }else {
                otpRepository.deleteAll(otpList);
            }
        }
    }

    private int otpGenerator() {
        return random.nextInt(100_000,999_999);
    }

    private String getEmailText(User user, int generatedOtp){
        return "Hello " + user.getUsername() + ",\n\n" +
                "Your OTP is : " + generatedOtp + "\n\n" +
                "This OTP will expire in 2 minutes.\n\n" +
                "Thanks,\n" +
                "CarForYou Team";
    }
}
