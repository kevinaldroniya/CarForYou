package com.car.foryou.dto.auth;

import com.car.foryou.model.User;
import lombok.Builder;

@Builder
public record RefreshTokenResponse(String token, User user) {
}
