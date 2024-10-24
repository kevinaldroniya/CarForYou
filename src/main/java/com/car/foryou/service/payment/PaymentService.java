package com.car.foryou.service.payment;

import com.car.foryou.dto.payment.PaymentRequest;

public interface PaymentService {
    String pay(Integer bidId, PaymentRequest paymentRequest);
    void setPaymentDetail(PaymentRequest paymentRequest);
}
