package com.car.foryou.dto.payment;

import com.car.foryou.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentType {
    DEPOSIT("deposit"),
    AUCTION("auction");

    private final String value;

    public static PaymentType fromString(String text){
        for (PaymentType type : PaymentType.values()){
            if (type.value.equalsIgnoreCase(text)){
                return type;
            }
        }
        throw new InvalidRequestException("No constant found with given value : " + text, HttpStatus.BAD_REQUEST);
    }
}
