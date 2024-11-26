package com.car.foryou.dto.auction;

import com.car.foryou.exception.InvalidRequestException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuctionProcessStatus {
    PENDING_CONFIRMATION("pending_confirmation"),
    CONFIRMATION_CANCELED("confirmation_canceled"),
    PAYMENT_PENDING("payment_pending"),
    PAYMENT_COMPLETED("payment_completed"),
    PAYMENT_CANCELED("payment_canceled");

    private final String value;

    AuctionProcessStatus(String value) {
        this.value = value;
    }

    public static AuctionProcessStatus fromString(String text){
        for (AuctionProcessStatus status : AuctionProcessStatus.values()){
            if (status.value.equalsIgnoreCase(text)){
                return status;
            }
        }
        throw new InvalidRequestException("No const found with given value : " + text, HttpStatus.BAD_REQUEST);
    }
}
