package com.car.foryou.service.auth;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auth.*;
import com.car.foryou.dto.email.EmailVerificationDto;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.otp.OtpResponse;
import com.car.foryou.dto.otp.OtpType;
import com.car.foryou.dto.otp.OtpVerificationRequest;
import com.car.foryou.dto.refreshtoken.RefreshTokenRequest;
import com.car.foryou.dto.refreshtoken.RefreshTokenResponse;
import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.exception.*;
import com.car.foryou.helper.EncryptionHelper;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import com.car.foryou.repository.group.GroupRepository;
import com.car.foryou.repository.user.UserRepository;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.otp.OtpService;
import com.car.foryou.mapper.UserMapper;
import com.car.foryou.service.refreshtoken.RefreshTokenServiceImpl;
import com.car.foryou.service.user.CustomUserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private final EncryptionHelper encryptionHelper;
    private final ObjectMapper objectMapper;

    public GeneralResponse<String> register(UserRequest request){
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
        User saved = userRepository.save(user);
        sendEmailVerification(saved.getEmail());
        return GeneralResponse.<String>builder()
                .message("User registered successfully, please check your email and verifying your email for further access")
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    public AuthResponse login(AuthLoginRequest request) {
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

    public AuthResponse getNewAccessToken(RefreshTokenRequest request) {
        RefreshTokenResponse refreshToken = refreshTokenServiceImpl.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.user();
        UserInfoDetails userInfoDetails = userMapper.mapUserToUserDetails(user);
        String accessToken = jwtService.generateToken(userInfoDetails, true);
        return AuthResponse.builder()
                .refreshToken(refreshToken.token())
                .accessToken(accessToken)
                .build();
    }

    public GeneralResponse<String> verifyEmail(String signature) {
      try {
          String jsonEmail = encryptionHelper.decrypt(signature);
          EmailVerificationDto emailVerificationDto = objectMapper.readValue(jsonEmail, new TypeReference<>() {
          });
          User user = userRepository.findByEmail(emailVerificationDto.getEmail()).orElseThrow(
                  () -> new InvalidRequestException("Your given token is invalid", HttpStatus.BAD_REQUEST)
          );
          if (user.isVerified()){
              return GeneralResponse.<String>builder()
                      .message("Email already verified, you can login now")
                      .data(null)
                      .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                      .build();
          }

          otpService.unSignOtpVerify(emailVerificationDto.getOtp(), emailVerificationDto.getEmail());
          user.setVerified(true);
          userRepository.save(user);
          return GeneralResponse.<String>builder()
                  .message("Email verified successfully, you can login now and enjoy our services")
                  .data(null)
                  .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                  .build();
      }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
              IllegalBlockSizeException | JsonProcessingException e){
          throw new GeneralException("There is an issue with your request, please try with a valid token", HttpStatus.BAD_REQUEST);
      }
    }

    public GeneralResponse<String> enableMfa(){
        Integer id = CustomUserDetailService.getLoggedInUserDetails().getId();
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        if (user.getPhoneNumber() == null){
            return GeneralResponse.<String>builder()
                    .message("Please add phone number first")
                    .data(null)
                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                    .build();
        }
        if (user.isMfaEnabled()){
            return GeneralResponse.<String>builder()
                    .message("MFA already enabled")
                    .data(null)
                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                    .build();
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
        String response = notificationService.sendNotification(NotificationChannel.WHATSAPP, "OTP Verification", message, user.getPhoneNumber());
        return GeneralResponse.<String>builder()
                .message(response)
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    public GeneralResponse<String> requestEmailVerification(String email){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("USER", "EMAIL", email)
        );
        sendEmailVerification(user.getEmail());
        return GeneralResponse.<String>builder()
                .message("Verification email sent successfully, please check your email and verifying your email for further access")
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    private void sendEmailVerification(String email){
        try {
            OtpResponse otp = otpService.generateOtp(email, OtpType.REGISTER);
            EmailVerificationDto emailVerificationDto = EmailVerificationDto.builder()
                    .email(email)
                    .otp(otp.getOtp())
                    .timeExpiration(otp.getTimeExpiration())
                    .build();
            String jsonEmailVerificationDto = objectMapper.writeValueAsString(emailVerificationDto);
            String encodeToString = encryptionHelper.encrypt(jsonEmailVerificationDto);
            String verificationLink = "http://localhost:8080/auth/verify?signature=" + encodeToString;
            MessageTemplate messageTemplate = MessageTemplate.builder()
                    .name("emailVerification")
                    .data(Map.of("verification_link", verificationLink))
                    .build();
            notificationService.sendNotification(NotificationChannel.EMAIL,"Email Verification", messageTemplate, email);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException | IOException e){
            throw new GeneralException("There is an error on our system, please try again later, if the problem persists, please contact our support team", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
