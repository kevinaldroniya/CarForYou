package com.car.foryou.dto.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    EXPIRED("EXPIRED"),
    FAILED("FAILED"),
    REFUND("REFUND"),
    CANCELLED("CANCELLED");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public static PaymentStatus fromString(String text){
        for (PaymentStatus paymentStatus : PaymentStatus.values()){
            if (paymentStatus.value.equalsIgnoreCase(text)){
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("No constant found with value : " + text);
    }
}
