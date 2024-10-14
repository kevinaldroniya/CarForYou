package com.car.foryou.service.refreshtoken;

import com.car.foryou.dto.refreshtoken.RefreshTokenResponse;

public interface RefreshTokenService {
    RefreshTokenResponse createRefreshToken(String username);
    RefreshTokenResponse verifyRefreshToken(String refreshToken);
}
