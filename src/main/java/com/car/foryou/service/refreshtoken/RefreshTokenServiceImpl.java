package com.car.foryou.service.refreshtoken;

import com.car.foryou.dto.refreshtoken.RefreshTokenResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.model.RefreshToken;
import com.car.foryou.repository.refreshtoken.RefreshTokenRepository;
import com.car.foryou.model.User;
import com.car.foryou.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshTokenResponse createRefreshToken(String username){
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("User not found with given username : " + username)
        );

        RefreshToken refreshToken = user.getRefreshToken();

        if (refreshToken == null){
            long refreshTokenValidity = 7*24*60*60*1000L;
            refreshToken = RefreshToken.builder()
                    .token(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }

        return RefreshTokenResponse.builder()
                .token(refreshToken.getToken())
                .user(refreshToken.getUser())
                .build();
    }

    public RefreshTokenResponse verifyRefreshToken(String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new RuntimeException("Refresh token not found with given token : " + refreshToken)
        );

        if (refToken.getExpirationTime().isBefore(Instant.now())){
            refreshTokenRepository.delete(refToken);
            throw new InvalidRequestException("Refresh token is expired, please login again", HttpStatus.UNAUTHORIZED);
        }
        return RefreshTokenResponse.builder()
                .token(refToken.getToken())
                .user(refToken.getUser())
                .build();
    }
}
