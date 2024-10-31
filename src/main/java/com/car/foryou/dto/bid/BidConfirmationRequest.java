package com.car.foryou.dto.bid;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidConfirmationRequest {
    private Integer bidId;
    private BidStatus bidStatus;
}
