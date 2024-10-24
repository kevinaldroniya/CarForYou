package com.car.foryou.service.payment;

import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentSetRequest;
import com.car.foryou.model.PaymentDetail;

public interface PaymentService {
    PaymentDetail getPaymentById(Integer id);
    String pay(Integer id, PaymentRequest paymentRequest);
    void setPaymentDetail(PaymentSetRequest request);
    String confirmPayment(Integer paymentId);
}
