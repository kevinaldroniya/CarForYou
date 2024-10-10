package com.car.foryou.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CancelRegistrationRequest(@NotNull String reason) {
}
