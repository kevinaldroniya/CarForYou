package com.car.foryou.service.payment;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.dto.payment.PaymentSetRequest;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.model.PaymentDetail;

import java.util.List;

public interface PaymentService {
    List<PaymentDetail> getAllPayments();
    PaymentDetail getPaymentById(Integer id);
    PaymentResponse getPaymentResponseById(Integer id);
    PaymentResponse getPaymentByUserIdAndItemId(Integer userId, Integer itemId);
    List<PaymentResponse> getAllPaymentsResponse();
    GeneralResponse<String> pay(Integer id, PaymentRequest paymentRequest);
    void setPaymentDetail(PaymentSetRequest request);
    GeneralResponse<String> confirmPayment(Integer paymentId);
    PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus);
}
