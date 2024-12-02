package com.car.foryou.service.payment;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.payment.*;
import com.car.foryou.model.Payment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface PaymentService {
    List<Payment> getAllPayments();
    Payment getPaymentById(Integer id);
    PaymentResponse getPaymentResponseById(Integer id);
    List<PaymentResponse> getAllPaymentsResponse();
//    GeneralResponse<String> manualPayment(Integer id, PaymentRequest paymentRequest);
    PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus);
    GeneralResponse<Map<String, Objects>> completeCallbackPayment(Integer paymentId);
    PaymentResponse payOnline(PaymentRequest paymentRequest);
    PaymentResponse callbackNotification(Map<String, Object> payload);
    PaymentResponse payOffline(Integer participantId, PaymentType paymentType);
}
