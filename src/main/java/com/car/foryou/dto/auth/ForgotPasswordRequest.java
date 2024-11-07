package com.car.foryou.dto.auth;

import lombok.Builder;
import lombok.Data;

@Builder
public record ForgotPasswordRequest (String email) {
}
