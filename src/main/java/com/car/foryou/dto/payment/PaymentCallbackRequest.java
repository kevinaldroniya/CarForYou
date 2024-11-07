package com.car.foryou.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCallbackRequest {
    private Boolean result;
    private String sid;
    private String trxId;
    private String status;
    private String tipe;
    private String paymentMethod;
    private String paymentChannel;
}
