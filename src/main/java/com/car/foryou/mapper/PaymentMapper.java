package com.car.foryou.mapper;

import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.model.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentMapper {

    public static PaymentResponse mapToResponse(Payment request){
        ZonedDateTime paymentTime = request.getPaymentTime() == null ? null : ZonedDateTime.ofInstant(request.getPaymentTime(), ZoneId.of("UTC"));
        ZonedDateTime paymentExpired =  request.getPaymentExpiration() == null ? null : ZonedDateTime.ofInstant(request.getPaymentExpiration(), ZoneId.of("UTC"));
        String[] split = request.getPaymentMethod().split("-");
        String paymentMethod = split[0];
        String paymentChannel = split[1];
        Map<String, Object> paymentCode = new HashMap<>();
        switch (paymentChannel){
            case "bca", "bni", "bri", "cimb", "permata" :
                paymentCode = Map.of(
                        "bank", paymentChannel,
                        "va_number", request.getPaymentCode()
                );
                break;
            case "mandiri":
                String[] splitCode = request.getPaymentCode().split("-");
                String billKey = splitCode[0];
                String billCode = splitCode[1];
                paymentCode = Map.of(
                        "bank", paymentChannel,
                        "billKey", billKey,
                        "billCode", billCode
                );
                break;
            default:
                throw new InvalidRequestException("No payment channel found with given value : " + paymentChannel, HttpStatus.BAD_REQUEST);
        }
        return PaymentResponse.builder()
                .paymentId(request.getId())
                .orderId(request.getOrderId())
                .userId(request.getUser().getId())
                .paymentAmount(request.getPaymentAmount())
                .paymentStatus(request.getPaymentStatus())
                .paymentTime(paymentTime)
                .paymentExpired(paymentExpired)
                .paymentMethod(paymentMethod)
                .paymentCode(paymentCode)
                .build();
    }
}
