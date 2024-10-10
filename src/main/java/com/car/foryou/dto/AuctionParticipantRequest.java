package com.car.foryou.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionParticipantRequest {
    private Integer depositAmount;
    private PaymentMethod paymentMethod;
}
