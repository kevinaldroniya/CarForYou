package com.car.foryou.service.impl;

import com.car.foryou.auth.service.JwtService;
import com.car.foryou.auth.util.UserInfoDetails;
import com.car.foryou.dto.EmailVerifyingRequest;
import com.car.foryou.dto.MailBody;
import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import com.car.foryou.repository.OtpRepository;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.EmailService;
import com.car.foryou.service.OtpService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
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

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService, JwtService jwtService) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
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
    public String verifyMyEmailByOtp(String authToken, EmailVerifyingRequest emailVerifyingRequest) {

        String tempJwt = authToken.substring(7);
        String username = jwtService.extractUsername(tempJwt);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Otp otp = otpRepository.findByOtpNumberAndUser(emailVerifyingRequest.getOtp(), user).orElseThrow(
                () -> new RuntimeException("Invalid OTP")
        );

        if (otp.getOtpExpiration() < ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()) {
            throw new RuntimeException("OTP expired");
        }



        UserInfoDetails userInfoDetails = mapToUserDetails(user);

//        otpRepository.delete(otp);
        // Generate a new JWT token with MFA authenticated flag
        return jwtService.generateToken(userInfoDetails, true);
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
