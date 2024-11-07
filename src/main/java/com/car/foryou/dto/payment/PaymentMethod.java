package com.car.foryou.dto.payment;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    VA("VA"),
    CC("CC"),
    BANK_TRANSFER("BANK_TRANSFER"),
    CASH("CASH");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public static PaymentMethod fromString(String text){
        for (PaymentMethod paymentMethod : PaymentMethod.values()){
            if (paymentMethod.value.equalsIgnoreCase(text)){
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("No constant found with value : " + text);
    }
}
