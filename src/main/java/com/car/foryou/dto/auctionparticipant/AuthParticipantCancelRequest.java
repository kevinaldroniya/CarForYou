package com.car.foryou.dto.auctionparticipant;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AuthParticipantCancelRequest(@NotNull String reason) {
}
