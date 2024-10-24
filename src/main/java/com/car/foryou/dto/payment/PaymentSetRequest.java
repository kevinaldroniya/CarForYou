package com.car.foryou.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentSetRequest {
    private Integer userId;
    private Integer itemId;
    private Long paymentAmount;
}
