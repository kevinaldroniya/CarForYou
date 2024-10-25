package com.car.foryou.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class PaymentResponse {
    private Integer paymentId;
    private Integer userId;
    private Integer itemId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Long paymentAmount;
    private String shippingAddress;
    private String shippingProvince;
    private String shippingCity;
    private String shippingPostalCode;
    private ZonedDateTime paymentTime;
    private ZonedDateTime paymentExpiration;
}
