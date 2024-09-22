package com.car.foryou.service.impl;

import com.car.foryou.dto.EmailVerifyingRequest;
import com.car.foryou.dto.MailBody;
import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import com.car.foryou.repository.OtpRepository;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.EmailService;
import com.car.foryou.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public String sendOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User with given email : " + email + ", not found")
        );

        int generatedOtp = otpGenerator();
        Otp otp = Otp.builder()
                .otpNumber(generatedOtp)
                .otpExpiration(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(60).toEpochSecond())
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
    public String verifyMyEmailByOtp(EmailVerifyingRequest request) {
        return "";
    }
}
