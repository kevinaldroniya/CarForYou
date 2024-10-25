package com.car.foryou.service.payment;

import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.dto.payment.PaymentSetRequest;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.model.PaymentDetail;

import java.util.List;

public interface PaymentService {
    PaymentResponse getPaymentById(Integer id);
    String pay(Integer id, PaymentRequest paymentRequest);
    void setPaymentDetail(PaymentSetRequest request);
    String confirmPayment(Integer paymentId);
    PaymentResponse getPaymentByUserIdAndItemId(Integer userId, Integer itemId);
    PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus);
    List<PaymentResponse> getAllPayments();
}
