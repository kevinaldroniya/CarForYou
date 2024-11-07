package com.car.foryou.service.payment;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.payment.*;
import com.car.foryou.model.PaymentDetail;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface PaymentService {
    List<PaymentDetail> getAllPayments();
    PaymentDetail getPaymentById(Integer id);
    PaymentResponse getPaymentResponseById(Integer id);
    PaymentResponse getPaymentByUserIdAndItemId(Integer userId, Integer itemId);
    List<PaymentResponse> getAllPaymentsResponse();
    GeneralResponse<String> manualPayment(Integer id, PaymentRequest paymentRequest);
    void setPaymentDetail(PaymentSetRequest request);
    GeneralResponse<String> confirmPayment(Integer paymentId);
    PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus);
    PaymentResponse cancelPayment(PaymentCancelRequest request);
    GeneralResponse<Map<String, Objects>> completeCallbackPayment(Integer paymentId);
}
