package com.car.foryou.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCancelRequest {
    private Integer bidId;
//    private PaymentStatus paymentStatus;
}
