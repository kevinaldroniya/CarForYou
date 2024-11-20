package com.car.foryou.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private Integer paymentId;
    private String orderId;
    private Integer userId;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private Long paymentAmount;
    private ZonedDateTime paymentTime;
    private PaymentType paymentType;
    private Map<String, Object> paymentCode;
    private ZonedDateTime paymentExpired;
}
