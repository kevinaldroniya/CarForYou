package com.car.foryou.service.otp;

import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.otp.OtpVerificationRequest;
import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.otp.OtpValidationRequest;
import com.car.foryou.dto.email.MailBody;
import com.car.foryou.mapper.UserMapper;
import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import com.car.foryou.repository.otp.OtpRepository;
import com.car.foryou.repository.user.UserRepository;
import com.car.foryou.service.auth.JwtService;
import com.car.foryou.service.email.EmailService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.transaction.Transactional;
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
    private final NotificationService notificationService;
    private static final int MAX_OTP_REQUEST = 5;


    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, EmailService emailService, JwtService jwtService, UserMapper userMapper, NotificationService notificationService) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
        this.random =  new SecureRandom();
    }

    @Override
    public Integer createOtp(OtpVerificationRequest request) {
        Integer id = CustomUserDetailService.getLoggedInUserDetails().getId();
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        List<Otp> otpList = otpRepository.findAllByUserAndOtpType(user, request.getOtpType());
        otpLimitCheck(otpList);

        int generatedOtp = otpGenerator();

        Otp otp = Otp.builder()
                .otpNumber(generatedOtp)
                .otpExpiration(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(120).toEpochSecond())
                .user(user)
                .otpType(request.getOtpType())
                .build();
        Otp save = otpRepository.save(otp);

        MessageTemplate message = MessageTemplate.builder()
                    .name("wa_otpRequest")
                    .data(Map.of("otp", otp.getOtpNumber()))
                    .build();
            notificationService.sendNotification(NotificationChannel.WHATSAPP, "OTP Verification", message, user.getPhoneNumber());



//        MailBody mailBody = MailBody.builder()
//                .to(user.getEmail())
//                .subject("Email Verification")
//                .text(getEmailText(user, generatedOtp))
//                .build();
//
//        emailService.sendSimpleMessage(mailBody);
        return save.getOtpNumber();
    }

    @Transactional
    @Override
    public String verifyOtp(OtpValidationRequest otpValidationRequest) {
        String username = CustomUserDetailService.getLoggedInUserDetails().getUsername();
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
        String response = "";
        switch (otp.getOtpType().getValue()){
            case "LOGIN":
                response = jwtService.generateToken(userInfoDetails, true);
                break;
            case "ENABLED_MFA":
                enableMfa(user);
                response = "Successfully enabled MFA";
                break;
            default:
                throw new RuntimeException("Invalid OTP type");
        }

        otpRepository.deleteAllByUserAndOtpType(user, otp.getOtpType());
        return response;
    }

    @Override
    public String verifyEmailOtp(String email, int otp) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User with given email : " + email + ", not found")
        );
        Otp validOtp = otpRepository.findByOtpNumberAndUser(otp, user).orElseThrow(
                () -> new RuntimeException("Invalid OTP")
        );
        if (validOtp.getOtpExpiration() < ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()) {
            throw new RuntimeException("OTP expired");
        }
        user.setVerified(true);
        userRepository.save(user);
        otpRepository.delete(validOtp);
        return "OK";
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

    private void enableMfa(User user){
        user.setMfaEnabled(true);
        userRepository.save(user);
    }

    private String getEmailText(User user, int generatedOtp){
        return "Hello " + user.getUsername() + ",\n\n" +
                "Your OTP is : " + generatedOtp + "\n\n" +
                "This OTP will expire in 2 minutes.\n\n" +
                "Thanks,\n" +
                "CarForYou Team";
    }
}
