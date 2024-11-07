package com.car.foryou.service.otp;

import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.otp.*;
import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.email.MailBody;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceExpiredException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.exception.TooManyRequestException;
import com.car.foryou.mapper.UserMapper;
import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import com.car.foryou.repository.otp.OtpRepository;
import com.car.foryou.repository.user.UserRepository;
import com.car.foryou.service.auth.JwtService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.refreshtoken.RefreshTokenService;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
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
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final SecureRandom random;
    private final NotificationService notificationService;
    private static final int MAX_OTP_REQUEST = 5;
    private static final String EMAIL = "EMAIL";


    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, RefreshTokenService refreshTokenService, JwtService jwtService, UserMapper userMapper, NotificationService notificationService) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
        this.random =  new SecureRandom();
    }

    @Override
    public Otp getOtpByOtpNumber(Integer otp) {
        return otpRepository.findByOtpNumber(otp).orElseThrow(
                () -> new ResourceNotFoundException("OTP", "OTP Number", otp)
        );
    }

    @Override
    public OtpResponse createOtp(OtpVerificationRequest request) {
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
        String sentNotification = notificationService.sendNotification(NotificationChannel.WHATSAPP, "OTP Verification", message, user.getPhoneNumber());
        return OtpResponse.builder()
                .otp(save.getOtpNumber())
                .otpType(save.getOtpType())
                .message(sentNotification)
                .build();
    }

    @Transactional
    @Override
    public OtpVerifyResponse verifyOtp(OtpValidationRequest otpValidationRequest) {
        String username = CustomUserDetailService.getLoggedInUserDetails().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Otp otp = otpRepository.findByOtpNumberAndUser(otpValidationRequest.getOtp(), user).orElseThrow(
                () -> new InvalidRequestException("Invalid OTP", HttpStatus.BAD_REQUEST)
        );
        if (otp.getOtpExpiration() < ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()) {
            throw new ResourceExpiredException("OTP expired");
        }
        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(user);
        String message;
        String refreshToken = null;
        String accessToken = null;
        switch (otp.getOtpType().getValue()){
            case "LOGIN":
                accessToken = jwtService.generateToken(userInfoDetails, true);
                refreshToken = refreshTokenService.createRefreshToken(username).token();
                message = "Successfully logged in";
                break;
            case "ENABLED_MFA":
                enableMfa(user);
                message = "Successfully enabled MFA";
                break;
            default:
                throw new InvalidRequestException("Invalid OTP type", HttpStatus.BAD_REQUEST);
        }
        otp.setIsUsed(true);
        otpRepository.save(otp);
        return new OtpVerifyResponse(message, accessToken, refreshToken);
    }

    @Override
    public String verifyEmailOtp(String email, int otp) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User with given email : " + email + ", not found")
        );
        Otp validOtp = otpRepository.findByOtpNumberAndUser(otp, user).orElseThrow(
                () -> new InvalidRequestException("Invalid OTP", HttpStatus.BAD_REQUEST)
        );
        if (validOtp.getOtpExpiration() < ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()) {
            throw new ResourceExpiredException("OTP expired");
        }
        user.setVerified(true);
        userRepository.save(user);
        otpRepository.delete(validOtp);
        return "OK";
    }

    @Override
    public OtpResponse generateOtp(String email, OtpType type) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("USER", EMAIL, email)
        );
        List<Otp> otpList = otpRepository.findAllByUserAndOtpType(user, type);
        otpLimitCheck(otpList);
        int generatedOtp = otpGenerator();
        Otp build = Otp.builder()
                .otpNumber(generatedOtp)
                .otpExpiration(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(24).toEpochSecond())
                .user(user)
                .otpType(type)
                .build();
        Otp save = otpRepository.save(build);
        return OtpResponse.builder()
                .otp(save.getOtpNumber())
                .otpType(save.getOtpType())
                .message(null)
                .timeExpiration(ZonedDateTime.ofInstant(Instant.ofEpochSecond(save.getOtpExpiration()), ZoneId.of("UTC")))
                .build();
    }

    @Override
    @Transactional
    public void unSignOtpVerify(Integer otp, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("USER", "EMAIL", email)
        );
        Otp foundedOtp = otpRepository.findByOtpNumberAndUser(otp, user).orElseThrow(
                () -> new ResourceNotFoundException("OTP", "request", otp)
        );
        if (foundedOtp.getOtpExpiration() < ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()) {
            throw new ResourceExpiredException("OTP expired");
        }

        if (foundedOtp.getIsUsed()) {
            throw new InvalidRequestException("OTP already used", HttpStatus.BAD_REQUEST);
        }

        foundedOtp.setIsUsed(true);
        otpRepository.save(foundedOtp);
    }

    @Override
    public Otp getOtpByUserAndOtpType(String email, OtpType otpType) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("USER", "EMAIL", email)
        );
        return  otpRepository.findByUserAndOtpType(user, otpType).orElseThrow(
                () -> new ResourceNotFoundException("OTP","User",user.getId())
        );
    }

    @Transactional
    @Override
    public void deleteOtp(Integer otp) {
        Otp founded = otpRepository.findByOtpNumber(otp).orElseThrow(
                () -> new ResourceNotFoundException("OTP", "request", otp)
        );
        otpRepository.delete(founded);
    }

    private void otpLimitCheck(List<Otp> otpList) {
        if (otpList.size() >= MAX_OTP_REQUEST){
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
            ZonedDateTime lastExpiration = ZonedDateTime.ofInstant(Instant.ofEpochSecond(otpList.get(otpList.size()-1).getOtpExpiration()), ZoneId.of("UTC"));
            if (now.isBefore(lastExpiration.plusHours(2))) {
                throw new TooManyRequestException("You have reached maximum OTP request, please try again later");
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
}
