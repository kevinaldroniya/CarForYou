package com.car.foryou.dto.auctionparticipant;

import com.car.foryou.dto.payment.PaymentMethod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionParticipantRequest {
    private Integer depositAmount;
    private PaymentMethod paymentMethod;
}
