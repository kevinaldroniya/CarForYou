package com.car.foryou.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    private PaymentMethod paymentMethod;
    private Long paymentAmount;
    private String shippingAddress;
    private String shippingCity;
    private String shippingProvince;
    private String shippingPostalCode;
}
