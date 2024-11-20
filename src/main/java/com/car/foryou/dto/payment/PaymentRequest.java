package com.car.foryou.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    private Integer participantId;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private String paymentChannel;
}
