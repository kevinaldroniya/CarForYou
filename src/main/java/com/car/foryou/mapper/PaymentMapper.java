package com.car.foryou.mapper;

import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.model.PaymentDetail;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class PaymentMapper {

    public static PaymentResponse mapToResponse(PaymentDetail request){
        ZonedDateTime paymentTime = request.getPaymentTime() == null ? null : ZonedDateTime.ofInstant(request.getPaymentTime(), ZoneId.of("UTC"));
        return PaymentResponse.builder()
                .paymentId(request.getId())
                .itemId(request.getItem().getId())
                .userId(request.getUser().getId())
                .paymentAmount(request.getPaymentAmount())
                .shippingProvince(request.getShippingProvince())
                .shippingCity(request.getShippingCity())
                .shippingPostalCode(request.getShippingPostalCode())
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .paymentExpiration(ZonedDateTime.ofInstant(request.getPaymentExpiration(), ZoneId.of("UTC")))
                .paymentTime(paymentTime)
                .build();
    }
}
