package com.car.foryou.service;

import com.car.foryou.dto.auth.RefreshTokenResponse;
import com.car.foryou.model.RefreshToken;

public interface RefreshTokenService {
    RefreshTokenResponse createRefreshToken(String username);
    RefreshTokenResponse verifyRefreshToken(String refreshToken);
}
