package com.car.foryou.dto.bid;

import lombok.Builder;
import lombok.Data;

@Builder
public record BidConfirmationRequest(Integer bidDetailId, BidStatus bidStatus) {
}
