package com.car.foryou.service.impl;

import com.car.foryou.dto.auth.RefreshTokenResponse;
import com.car.foryou.model.RefreshToken;
import com.car.foryou.repository.RefreshTokenRepository;
import com.car.foryou.model.User;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.RefreshTokenService;
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

        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh token is expired");
        }
        return RefreshTokenResponse.builder()
                .token(refToken.getToken())
                .user(refToken.getUser())
                .build();
    }
}
